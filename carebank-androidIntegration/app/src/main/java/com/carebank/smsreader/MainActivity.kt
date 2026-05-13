package com.carebank.smsreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var currentMessages = listOf<SmsInfo>()
    private val WEBHOOK_URL = "http://192.168.1.7:3000/api/sms/batch"
    private val HEALTH_URL  = "http://192.168.1.7:3000/health"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            addDebugLogToJS("✅ SMS Permission Granted")
            loadAllSmsMessages()
        } else {
            addDebugLogToJS("❌ SMS Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.settings.javaScriptEnabled  = true
        webView.settings.domStorageEnabled  = true
        webView.settings.allowFileAccess    = true
        webView.settings.allowContentAccess = true

        // ✅ FIXED: interface name changed from "Android" → "AndroidSms"
        //    so the frontend's  AndroidSms.xxx()  calls now resolve correctly
        webView.addJavascriptInterface(WebAppInterface(), "AndroidSms")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript(
                    "if(window.onAndroidReady) window.onAndroidReady();", null
                )
            }
        }

        webView.loadUrl("file:///android_asset/frontend/index.html")
        setContentView(webView)
        checkPermissions()
    }

    // ─────────────────────────────────────────────
    // JavaScript Interface
    // ─────────────────────────────────────────────
    inner class WebAppInterface {

        // Called by frontend: AndroidSms.testServerConnection()
        @JavascriptInterface
        fun testServerConnection() {
            addDebugLogToJS("🔌 Testing server connection...")
            Thread {
                try {
                    val url        = URL(HEALTH_URL)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000

                    val responseCode = connection.responseCode
                    val response = if (responseCode == 200)
                        connection.inputStream.bufferedReader().readText()
                    else
                        "Error: $responseCode"

                    val result = JSONObject().apply {
                        put("success",    responseCode == 200)
                        put("statusCode", responseCode)
                        put("response",   response.take(200))
                    }

                    addDebugLogToJS(
                        if (responseCode == 200)
                            "✅ Server connected! ($responseCode)"
                        else
                            "❌ Server error: $responseCode"
                    )

                    // ✅ Calls the matching frontend callback
                    runOnUiThread {
                        webView.evaluateJavascript(
                            "javascript:onServerTestResult('${result.toString().replace("'", "\\'")}')",
                            null
                        )
                    }
                    connection.disconnect()

                } catch (e: Exception) {
                    addDebugLogToJS("❌ Connection failed: ${e.message}")
                    runOnUiThread {
                        webView.evaluateJavascript(
                            "javascript:onServerTestResult('{\"success\":false,\"error\":\"${e.message}\"}')",
                            null
                        )
                    }
                }
            }.start()
        }

        // Called by frontend: AndroidSms.loadSmsMessages()
        // ─────────────────────────────────────────────
// REPLACE ONLY these two functions in MainActivity.kt
// ─────────────────────────────────────────────

        // Called by frontend: AndroidSms.loadSmsMessages()
        @JavascriptInterface
        fun loadSmsMessages() {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                addDebugLogToJS("❌ No SMS permission — cannot load messages")
                return
            }

            addDebugLogToJS("📱 Reading SMS from device...")
            Thread {
                currentMessages = getAllSmsMessages()

                val messagesArray = JSONArray()
                currentMessages.forEach { msg ->
                    messagesArray.put(JSONObject().apply {
                        put("id",           msg.timestamp)
                        put("sender",       msg.address)
                        put("message",      msg.body)       // JSONObject.put() escapes this safely
                        put("timestamp",    msg.timestamp)
                        put("dateFormatted",
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(msg.timestamp))
                        )
                    })
                }

                val result = JSONObject().apply {
                    put("success",  true)
                    put("count",    currentMessages.size)
                    put("messages", messagesArray)
                }

                val jsonString = result.toString()
                addDebugLogToJS("✅ Loaded ${currentMessages.size} SMS messages")

                runOnUiThread {
                    // ✅ FIX: Store JSON in a window variable first, then call the function
                    // This avoids ALL string escaping issues with SMS message content
                    webView.evaluateJavascript(
                        """
                (function() {
                    window.__smsPayload = $jsonString;
                    if (typeof onSmsLoaded === 'function') {
                        onSmsLoaded(window.__smsPayload);
                    }
                })();
                """.trimIndent(),
                        null
                    )
                }
            }.start()
        }

        // Called by frontend: AndroidSms.sendSmsToServer()
        @JavascriptInterface
        fun sendSmsToServer() {
            if (currentMessages.isEmpty()) {
                addDebugLogToJS("❌ No messages loaded yet — click LOAD SMS first")
                runOnUiThread {
                    webView.evaluateJavascript("javascript:onSmsSent(false)", null)
                }
                return
            }

            addDebugLogToJS("📤 Sending ${currentMessages.size} messages to server...")
            Thread {
                val success = sendBatchToWebhook(currentMessages)

                addDebugLogToJS(
                    if (success) "✅ SMS sent to server successfully!"
                    else         "❌ Failed to send SMS to server"
                )

                runOnUiThread {
                    webView.evaluateJavascript("javascript:onSmsSent($success)", null)
                }
            }.start()
        }

        // Called by frontend: AndroidSms.getSmsData()
        // Returns the raw JSON string directly (used in requestSmsFromAndroid)
        @JavascriptInterface
        fun getSmsData(): String {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return "[]"
            }

            if (currentMessages.isEmpty()) {
                currentMessages = getAllSmsMessages()
            }

            val arr = JSONArray()
            currentMessages.forEach { msg ->
                arr.put(JSONObject().apply {
                    put("id",            msg.timestamp)
                    put("sender",        msg.address)
                    put("message",       msg.body)
                    put("timestamp",     msg.timestamp)
                    put("dateFormatted",
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(msg.timestamp))
                    )
                })
            }
            return arr.toString()
        }

        // Called by frontend: AndroidSms.refreshSms()
        @JavascriptInterface
        fun refreshSms() {
            loadSmsMessages()
        }

        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun getDeviceInfo(): String {
            return JSONObject().apply {
                put("ip", getLocalIpAddress())
                put("android_id",
                    android.provider.Settings.Secure.getString(
                        contentResolver, android.provider.Settings.Secure.ANDROID_ID
                    )
                )
            }.toString()
        }
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────
    private fun addDebugLogToJS(message: String) {
        runOnUiThread {
            val escaped = message.replace("'", "\\'").replace("\n", " ")
            webView.evaluateJavascript(
                "javascript:if(typeof addDebugLog==='function') addDebugLog('$escaped','info');",
                null
            )
        }
    }

    private fun getLocalIpAddress(): String {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val ni = interfaces.nextElement()
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") != true)
                        return addr.hostAddress ?: "Unknown"
                }
            }
            "127.0.0.1"
        } catch (e: Exception) { "Error: ${e.message}" }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) permissions.add(Manifest.permission.READ_SMS)

        if (permissions.isNotEmpty()) requestPermissionLauncher.launch(permissions.toTypedArray())
        else loadAllSmsMessages()
    }

    private fun loadAllSmsMessages() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            addDebugLogToJS("❌ No SMS permission")
            return
        }
        Thread {
            currentMessages = getAllSmsMessages()
            addDebugLogToJS("✅ Auto-loaded ${currentMessages.size} SMS messages on startup")
        }.start()
    }

    private fun getAllSmsMessages(): List<SmsInfo> {
        val smsMessages = mutableListOf<SmsInfo>()
        try {
            val cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
                null, null,
                "${Telephony.Sms.DATE} DESC"
            )
            cursor?.use {
                val addrIndex  = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIndex  = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIndex  = it.getColumnIndex(Telephony.Sms.DATE)
                while (it.moveToNext()) {
                    val address   = if (addrIndex  >= 0) it.getString(addrIndex)  ?: "Unknown" else "Unknown"
                    val body      = if (bodyIndex  >= 0) it.getString(bodyIndex)  ?: ""        else ""
                    val timestamp = if (dateIndex  >= 0) it.getLong(dateIndex)                 else 0L
                    if (body.isNotBlank()) smsMessages.add(SmsInfo(address, body, timestamp))
                }
            }
        } catch (e: Exception) {
        } catch (e: Exception) {
            addDebugLogToJS("❌ Error reading SMS: ${e.message}")
        }
        return smsMessages
    }

    private fun sendBatchToWebhook(messages: List<SmsInfo>): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            val smsArray = JSONArray()
            messages.forEach { msg ->
                smsArray.put(JSONObject().apply {
                    put("sender",             msg.address)
                    put("message",            msg.body)
                    put("timestamp",          msg.timestamp)
                    put("timestamp_readable",
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date(msg.timestamp))
                    )
                    put("device_id",
                        android.provider.Settings.Secure.getString(
                            contentResolver, android.provider.Settings.Secure.ANDROID_ID
                        )
                    )
                })
            }

            val payload = JSONObject().apply {
                put("total_count", messages.size)
                put("device_id",
                    android.provider.Settings.Secure.getString(
                        contentResolver, android.provider.Settings.Secure.ANDROID_ID
                    )
                )
                put("timestamp", System.currentTimeMillis())
                put("messages",  smsArray)
            }

            val payloadString = payload.toString()
            addDebugLogToJS("📦 Payload: ${payloadString.length / 1024} KB")

            val url = URL(WEBHOOK_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput      = true
            connection.connectTimeout = 60000
            connection.readTimeout   = 120000

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payloadString)
                writer.flush()
            }

            val responseCode = connection.responseCode
            addDebugLogToJS("📡 Server response: $responseCode")
            responseCode in 200..299

        } catch (e: Exception) {
            addDebugLogToJS("❌ Send failed: ${e.message}")
            false
        } finally {
            connection?.disconnect()
        }
    }

    data class SmsInfo(
        val address:   String,
        val body:      String,
        val timestamp: Long
    )
}
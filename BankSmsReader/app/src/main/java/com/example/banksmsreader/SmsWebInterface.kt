package com.example.banksmsreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONArray
import org.json.JSONObject

class SmsWebInterface(
    private val context: Context,
    private val contentResolver: android.content.ContentResolver
) {

    private var webView: WebView? = null
    // Use LinkedHashMap keyed by id to prevent duplicates automatically
    private val smsMap = LinkedHashMap<Long, BankSmsMessage>()
    private val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    init {
        registerSmsReceiver()
    }

    fun setWebView(webView: WebView) {
        this.webView = webView
    }

    private fun registerSmsReceiver() {
        val filter = IntentFilter("NEW_SMS_RECEIVED")
        localBroadcastManager.registerReceiver(smsBroadcastReceiver, filter)
    }

    private val smsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "NEW_SMS_RECEIVED") {
                val id        = intent.getLongExtra("sms_id", System.currentTimeMillis())
                val sender    = intent.getStringExtra("sender") ?: "Unknown"
                val body      = intent.getStringExtra("body") ?: ""
                val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

                val newSms = BankSmsMessage(
                    id      = id,
                    address = sender,
                    body    = body,
                    date    = timestamp,
                    type    = 1
                )

                // LinkedHashMap deduplicates by id automatically
                smsMap[id] = newSms

                try {
                    val json = JSONObject().apply {
                        put("id",       newSms.id)
                        put("merchant", newSms.parseMerchant())
                        put("date",     newSms.getFormattedDate())
                        put("category", newSms.getCategory())
                        put("status",   newSms.getStatus())
                        put("type",     newSms.getTransactionType())
                        put("amount",   newSms.parseAmount() ?: 0.0)
                    }.toString().replace("\\", "\\\\").replace("'", "\\'")

                    webView?.post {
                        webView?.evaluateJavascript("addNewTransaction('$json')", null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @JavascriptInterface
    fun getSmsTransactions(): String {
        // Always read fresh from SMS inbox — no stale cache
        readSmsMessages()

        val jsonArray = JSONArray()
        // Return ALL messages, no .take() limit
        for (sms in smsMap.values.sortedByDescending { it.date }) {
            try {
                val amount = sms.parseAmount() ?: 0.0
                if (amount > 0) {
                    jsonArray.put(JSONObject().apply {
                        put("id",       sms.id)
                        put("merchant", sms.parseMerchant())
                        put("date",     sms.getFormattedDate())
                        put("category", sms.getCategory())
                        put("status",   sms.getStatus())
                        put("type",     sms.getTransactionType())
                        put("amount",   amount)
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return jsonArray.toString()
    }

    private fun readSmsMessages() {
        val newMap = LinkedHashMap<Long, BankSmsMessage>()

        // FIXED: Don't put LIMIT in the sort order string — use a proper query
        // Query ALL messages, no artificial limit
        val cursor: Cursor? = contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"  // FIXED: no LIMIT here, get everything
        )

        cursor?.use {
            val idCol      = it.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addressCol = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyCol    = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateCol    = it.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val typeCol    = it.getColumnIndexOrThrow(Telephony.Sms.TYPE)

            while (it.moveToNext()) {
                try {
                    val sms = BankSmsMessage(
                        id      = it.getLong(idCol),
                        address = it.getString(addressCol) ?: "Unknown",
                        body    = it.getString(bodyCol) ?: "",
                        date    = it.getLong(dateCol),
                        type    = it.getInt(typeCol)
                    )
                    // Only store bank transactions with a valid amount
                    if (sms.isBankTransaction() && sms.parseAmount() != null) {
                        newMap[sms.id] = sms  // id-keyed, so no duplicates ever
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Replace map contents — clears stale data and repopulates fresh
        smsMap.clear()
        smsMap.putAll(newMap)
    }

    @JavascriptInterface
    fun refreshTransactions(): String {
        readSmsMessages()
        return "refreshed"
    }

    fun cleanup() {
        try {
            localBroadcastManager.unregisterReceiver(smsBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
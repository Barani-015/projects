package com.example.banksmsreader

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.banksmsreader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val SMS_PERMISSION_CODE = 100
    private lateinit var smsReceiver: SmsReceiver
    private lateinit var smsWebInterface: SmsWebInterface  // FIXED: keep reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        smsReceiver = SmsReceiver()
        registerSmsReceiver()
        checkSmsPermission()
    }

    private fun registerSmsReceiver() {
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(smsReceiver, filter)
        }
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            requestSmsPermission()
        } else {
            setupWebView()
        }
    }

    private fun requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            AlertDialog.Builder(this)
                .setTitle("SMS Permission Needed")
                .setMessage("BankSmsReader needs SMS access to track your bank transactions.")
                .setPositiveButton("Grant") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), SMS_PERMISSION_CODE)
                }
                .setNegativeButton("Cancel") { _, _ -> setupWebView() }
                .show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), SMS_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) setupWebView()
    }

    private fun setupWebView() {
        // FIXED: create interface first, then pass WebView reference into it
        smsWebInterface = SmsWebInterface(this, contentResolver)

        with(binding.webView) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.allowFileAccess = true
            settings.allowContentAccess = true

            addJavascriptInterface(smsWebInterface, "AndroidSmsInterface")

            // FIXED: give interface a WebView reference for real-time pushes
            smsWebInterface.setWebView(this)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // FIXED: use evaluateJavascript (not loadUrl) — safer and correct
                    view?.evaluateJavascript("loadSmsTransactions()", null)
                }
            }

            loadUrl("file:///android_asset/index.html")
        }
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            binding.webView.evaluateJavascript("refreshTransactionsFromSms()", null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(smsReceiver) } catch (e: Exception) { e.printStackTrace() }
        if (::smsWebInterface.isInitialized) smsWebInterface.cleanup()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }
}
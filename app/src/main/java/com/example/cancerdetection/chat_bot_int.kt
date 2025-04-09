package com.example.cancerdetection

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class chat_bot_int : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot_int)
        val webView: WebView = findViewById(R.id.botview)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://hope-chatbot-three.vercel.app/")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val isKeyboardVisible = imeInsets.bottom > 0
            if (isKeyboardVisible) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, imeInsets.bottom)
            } else {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            }
            insets
        }
    }
}
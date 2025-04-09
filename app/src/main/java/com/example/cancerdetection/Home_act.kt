package com.example.cancerdetection
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.HorizontalScrollView
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Home_act : AppCompatActivity() {

    private lateinit var tipsScrollView: HorizontalScrollView
    private val handler = Handler()
    private val scrollRunnable = object : Runnable {
        override fun run() {
            // Scroll horizontally by 3 pixels
            tipsScrollView.scrollBy(3, 0)

            // Check if we've reached the end and reset the scroll position to the start
            if (tipsScrollView.scrollX >= tipsScrollView.getChildAt(0).width - tipsScrollView.width) {
                tipsScrollView.scrollTo(0, 0) // Reset to the start
            }

            handler.postDelayed(this, 25)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cancerPred = findViewById<Button>(R.id.lung_cancer_pred)
        cancerPred.setOnClickListener {
            val intent = Intent(this, cancerPrediction::class.java)
            startActivity(intent)
        }

        val info=findViewById<Button>(R.id.info_but)
        info.setOnClickListener{
            val info_act= Intent(this,info_lung::class.java)
            startActivity(info_act)
        }

        val hope_bot = findViewById<Button>(R.id.add_info)
        hope_bot.setOnClickListener {
            val hope = Intent(this, chat_bot_int::class.java)
            startActivity(hope)
        }

        val userWelcome = findViewById<TextView>(R.id.user_welc)
        val intent = intent
        val name = intent.getStringExtra("name")
        userWelcome.text = "$name"

        val profileImage=findViewById<ImageView>(R.id.prof)

        profileImage.setOnClickListener {
            showProfileMenu(it)
        }

        //webview setup

        val videoIds = listOf(
            "n6TE-Nkws6Q",
            "lLjMF5RHxf8",
            "uirINayrSJs",
            "HNkCuV9kU1Y",
            "O61vFLsNBqM",
            "_RaSc5jgbBg",
            "Al3xpbQ2rA4",
            "j31T14gPEOo",
            "sq2dfgg70hs",
            "ihCnDjyJv5c",
            "Y18Vz51Nkos",
            "PFdSRBME5Bs",
            "Qr6zj1aw3PU",
            "DQILqJ5i0Gw",
            "QVkhOK5ykm8"
        )

        val webView: WebView = findViewById(R.id.v1)
        val webView2: WebView = findViewById(R.id.v2)
        val webView3: WebView = findViewById(R.id.v3)

        webView.settings.javaScriptEnabled = true
        webView2.settings.javaScriptEnabled = true
        webView3.settings.javaScriptEnabled = true

        var currentIndex = 0

        fun loadVideos() {
            val id1 = videoIds[currentIndex % videoIds.size]
            val id2 = videoIds[(currentIndex + 1) % videoIds.size]
            val id3 = videoIds[(currentIndex + 2) % videoIds.size]

            webView.loadData(generateIframe(id1), "text/html", "utf-8")
            webView2.loadData(generateIframe(id2), "text/html", "utf-8")
            webView3.loadData(generateIframe(id3), "text/html", "utf-8")

            currentIndex = (currentIndex + 3) % videoIds.size
        }

        val handler = Handler(Looper.getMainLooper())
        val rotateRunnable = object : Runnable {
            override fun run() {
                loadVideos()
                handler.postDelayed(this, 20000)
            }
        }
        handler.post(rotateRunnable)


        // Get the HorizontalScrollView for cancer tips
        tipsScrollView = findViewById(R.id.tips_scroll)

        // Start the auto-scrolling when the activity is created
        handler.post(scrollRunnable)

        // Setup the additional button to open a Google search for "Cancer Centers Near Me"
        val additionalButton = findViewById<Button>(R.id.additional_button)
        additionalButton.setOnClickListener {
            val query = "Cancer Centers Near Me"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(scrollRunnable)
    }

    fun generateIframe(videoId: String): String {
        return """
        <iframe width="100%" height="100%" 
        src="https://www.youtube.com/embed/$videoId"
        frameborder="0" allowfullscreen></iframe>
    """.trimIndent()
    }

    private fun showProfileMenu(anchor: android.view.View) {
        val popupMenu = PopupMenu(this, anchor)
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")

        popupMenu.menu.apply {
            add("Name : $name")
            add("Email : $email")
            add("Sign Out")
            add("About The Creators")
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Sign Out" -> showSignOutDialog()
                "About The Creators" -> {
                    val aboutUsIntent = Intent(this, about_us::class.java)
                    startActivity(aboutUsIntent)
                }
            }
            true
        }

        popupMenu.show()
    }
    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { dialog, _ ->
                handleSignOut()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun handleSignOut() {
        FirebaseAuth.getInstance().signOut()
        // Remove "isLoggedIn" flag
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isLoggedIn", false).apply()

        val intent = Intent(this, login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

//    override fun onStart() {
//        super.onStart()
//
//        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
//        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null && isLoggedIn) {
//            val intent = Intent(this, Home_act::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
}
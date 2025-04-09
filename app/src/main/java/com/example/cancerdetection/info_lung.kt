package com.example.cancerdetection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cancerdetection.databinding.ActivityInfoLungBinding

class info_lung : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInfoLungBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoLungBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val what = findViewById<TextView>(R.id.section_title1)
        what.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.clevelandclinic.org/health/diseases/4375-lung-cancer"))
            startActivity(intent)
        }
        val symp = findViewById<TextView>(R.id.section_title2)
        symp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://medlineplus.gov/lungcancer.html#:~:text=Lung%20cancer%20is%20cancer%20that,differently%20and%20are%20treated%20differently."))
            startActivity(intent)
        }
        val risk = findViewById<TextView>(R.id.section_title3)
        risk.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hopkinsmedicine.org/health/conditions-and-diseases/lung-cancer/lung-cancer-risk-factors#:~:text=Approximately%2090%20percent%20of%20lung,chemicals%20like%20radon%20and%20asbestos."))
            startActivity(intent)
        }
        val treat = findViewById<TextView>(R.id.section_title4)
        treat.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cdc.gov/lung-cancer/treatment/index.html#:~:text=it%20has%20spread.-,People%20with%20non%2Dsmall%20cell%20lung%20cancer%20can%20be%20treated,with%20radiation%20therapy%20and%20chemotherapy."))
            startActivity(intent)
        }
        val more_info = findViewById<TextView>(R.id.section_title5)
        more_info.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cancer.org/"))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_info_lung)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
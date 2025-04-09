package com.example.cancerdetection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db=Firebase.firestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth=FirebaseAuth.getInstance()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val currentUser = auth.currentUser

        if (currentUser != null && isLoggedIn) {
            // User is already signed in and hasn’t manually signed out
            val homeIntent = Intent(this, Home_act::class.java)
            val userId = currentUser.uid
            val userRef = db.collection("user").document(userId)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    homeIntent.putExtra("name", name)
                    homeIntent.putExtra("email", currentUser.email)
                    startActivity(homeIntent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val emailfield=findViewById<EditText>(R.id.email_log)
        val passfield=findViewById<EditText>(R.id.password_log)
        val button_log=findViewById<Button>(R.id.button_log)
        val prog=findViewById<ProgressBar>(R.id.progress_login)
        val forgot=findViewById<TextView>(R.id.forgot)
        val pass_vis=findViewById<ImageView>(R.id.pass_vis)
        var count:Int=0

        pass_vis.setOnClickListener{
            count+=1
            if(count%2==1){
                pass_vis.setImageResource(R.drawable.pass_shown)
                passfield.transformationMethod=HideReturnsTransformationMethod.getInstance()
            }
            else{
                pass_vis.setImageResource(R.drawable.pass_hidden)
                passfield.transformationMethod=PasswordTransformationMethod.getInstance()
            }
            if(count==9){
                count=1
            }
            passfield.setSelection(passfield.text.length)
        }
        
        forgot.setOnClickListener{
            val user_mail=emailfield.text.toString()
            if(user_mail.isNotEmpty()){
                auth.sendPasswordResetEmail(user_mail)
                    .addOnSuccessListener {
                        Toast.makeText(this, "An Email has been sent to reset your Password", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Couldn't send forgot password Email", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        button_log.setOnClickListener{
            val email=emailfield.text.toString()
            val password=passfield.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                prog.visibility=View.VISIBLE
                login(email,password)
            }
            else{
                prog.visibility=View.INVISIBLE
                Toast.makeText(this,"Please enter email and password",Toast.LENGTH_SHORT).show()
            }
        }


        val t1=findViewById<TextView>(R.id.sign)
        t1.setOnClickListener{
            val exp_signup= Intent(this,MainActivity2::class.java)
            startActivity(exp_signup)
        }
    }
    private fun login(email: String,password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userid = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userid)
                ref.get().addOnSuccessListener {
                    if (it != null) {
                        val prog = findViewById<ProgressBar>(R.id.progress_login)
                        val name = it.data?.get("name")?.toString()

                        // ✅ Save login flag in SharedPreferences
                        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("isLoggedIn", true).apply()

                        Toast.makeText(this, "Welcome $name", Toast.LENGTH_SHORT).show()
                        val home_intent = Intent(this, Home_act::class.java)
                        home_intent.putExtra("name", name)
                        home_intent.putExtra("email", email)
                        startActivity(home_intent)
                        prog.visibility = View.INVISIBLE
                        finish()
                    }
                }
            } else {
                val prog = findViewById<ProgressBar>(R.id.progress_login)
                prog.visibility = View.INVISIBLE
                Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

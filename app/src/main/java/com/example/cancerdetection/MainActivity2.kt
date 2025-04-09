package com.example.cancerdetection

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
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cancerdetection.databinding.ActivityMain2Binding
import com.example.cancerdetection.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class User(
    val name: String,
    val dob: String,
    val phone: String,
    val email: String,
    val password: String
)

class MainActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val unfield=findViewById<EditText>(R.id.username_su)
        val dobfield=findViewById<EditText>(R.id.dob_su)
        val phonefield=findViewById<EditText>(R.id.phone_su)
        val emailfield=findViewById<EditText>(R.id.email_su)
        val passfield=findViewById<EditText>(R.id.password_su)
        val but_su=findViewById<Button>(R.id.button_su)
        val back=findViewById<FloatingActionButton>(R.id.back2)
        val prog=findViewById<ProgressBar>(R.id.progress_signup)
        val pass_vis_su=findViewById<ImageView>(R.id.pass_vis_su)
        var count:Int=0

        pass_vis_su.setOnClickListener{
            count+=1
            if(count%2==1){
                pass_vis_su.setImageResource(R.drawable.pass_shown)
                passfield.transformationMethod= HideReturnsTransformationMethod.getInstance()
            }
            else{
                pass_vis_su.setImageResource(R.drawable.pass_hidden)
                passfield.transformationMethod= PasswordTransformationMethod.getInstance()
            }
            if(count==9){
                count=1
            }
            passfield.setSelection(passfield.text.length)
        }

        back.setOnClickListener{
            finish()
        }

        but_su.setOnClickListener{
            val name=unfield.text.toString()
            val dob=dobfield.text.toString()
            val phone=phonefield.text.toString()
            val email=emailfield.text.toString()
            val password=passfield.text.toString()

            if(name.isNotEmpty() && dob.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                prog.visibility=View.VISIBLE
                register(name,dob,phone,email,password)
            }
            else{
                prog.visibility=View.INVISIBLE
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }
        }

        val t2=findViewById<TextView>(R.id.log)
        t2.setOnClickListener{
            this.finish()
        }
    }
    // REGISTER EMAIL AND PASSWORD FOR LOGIN
    fun register(name: String, dob: String, phone:String, email: String, password: String){
        val prog=findViewById<ProgressBar>(R.id.progress_signup)
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                prog.visibility=View.VISIBLE
                // ADD VALUE TO THE FIRESTORE DATABASE
                val userMap= hashMapOf(
                    "name" to name,
                    "dob" to dob,
                    "phone_no" to phone,
                    "email" to email
                )
                val userId=auth.currentUser?.uid ?:"unknown_id"
                db.collection("user").document(userId).set(userMap)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            prog.visibility=View.INVISIBLE
                            Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            prog.visibility=View.INVISIBLE
                            Toast.makeText(this, "User Not Registered", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener{exception->
                        prog.visibility=View.INVISIBLE
                        Toast.makeText(this,"Error: ${exception.message}",Toast.LENGTH_LONG).show()
                    }
                val userid=auth.currentUser?.uid
                val database=FirebaseDatabase.getInstance().getReference("users")
                val user=User(name,dob,phone,email,password)
                userid?.let{database.child(it).setValue(user)}?.addOnCompleteListener{dbtask ->
                    if(dbtask.isSuccessful){
                        Toast.makeText(this,"Registration Complete",Toast.LENGTH_SHORT).show()
                        val home_intent=Intent(this, Home_act::class.java)
                        startActivity(home_intent)
                        prog.visibility=View.INVISIBLE
                    }
                    else{
                        prog.visibility=View.INVISIBLE
                        Toast.makeText(this,"Failed to save user data",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                prog.visibility=View.INVISIBLE
                Toast.makeText(this,"Registration failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
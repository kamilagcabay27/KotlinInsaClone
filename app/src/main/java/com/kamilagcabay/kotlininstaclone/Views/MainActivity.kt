package com.kamilagcabay.kotlininstaclone.Views

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kamilagcabay.kotlininstaclone.R
import com.kamilagcabay.kotlininstaclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        val user = auth.currentUser

        if (user != null){
            val intent = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun signInClicked(view : View) {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (email.equals("") || password.equals("")){
            Toast.makeText(this@MainActivity,"Please check your email or password!!",Toast.LENGTH_LONG).show()

        } else{
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {authResult ->
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener{e ->
                Toast.makeText(this@MainActivity,"Access Denied",Toast.LENGTH_LONG).show()

            }
        }

    }

    fun signUpClicked(view: View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        //thats another code block
        //if(email.isNotEmpty() && password.isNotEmpty()){} diyerek altına aynı işlemeleri yapabiliriz.

        if (email.equals("") || password.equals("")) {

            Toast.makeText(this@MainActivity,"Please check your email/password!!",Toast.LENGTH_LONG).show()

        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {authResult ->
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener{e ->
                Toast.makeText(this@MainActivity,"Access Denied",Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.info_app){
            val alert = AlertDialog.Builder(this@MainActivity)
            alert.setTitle("Title")
            alert.setMessage("This is the test Alert Dialog")
            alert.setPositiveButton("Close",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Toast.makeText(this@MainActivity,"Have a fun",Toast.LENGTH_LONG).show()
                }
            })

            alert.show()
        }
        return super.onOptionsItemSelected(item)
    }



}
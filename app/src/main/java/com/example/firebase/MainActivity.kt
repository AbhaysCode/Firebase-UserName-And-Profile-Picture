package com.example.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var tvLoggedIn:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        tvLoggedIn = findViewById<TextView>(R.id.tvLoggedIn)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnRegister.setOnClickListener {
            registerUser()
        }
        btnLogin.setOnClickListener {
            loginUser()
        }
    }
    private fun registerUser(){
        val email = findViewById<EditText>(R.id.etEmailRegister).text.toString()
        val password = findViewById<EditText>(R.id.etPasswordRegister).text.toString()
        GlobalScope.launch(Dispatchers.IO){
            try {
                auth.createUserWithEmailAndPassword(email,password)
                withContext(Dispatchers.Main){
                    checkLogInState()
                }
            }catch (e:Exception){
                Log.d("MainActivity","Exception - $e")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.signOut()
        checkLogInState()
    }
    private fun loginUser(){
        val email = findViewById<EditText>(R.id.etEmailLogin).text.toString()
        val password = findViewById<EditText>(R.id.etPasswordLogin).text.toString()
        GlobalScope.launch(Dispatchers.IO){
            try {
                auth.signInWithEmailAndPassword(email,password)
                withContext(Dispatchers.Main){
                    checkLogInState()
                }
            }catch (e:Exception){
                Log.d("MainActivity","Exception - $e")
            }
        }
    }

    private fun checkLogInState() {
        if (auth.currentUser == null){
            tvLoggedIn.text = "You are not logged in"
        }else{
            tvLoggedIn.text = "You are logged in"
        }
    }
}
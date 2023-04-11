package com.example.firebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var tvLoggedIn:TextView
    lateinit var ivProfilePicture: ImageView
    lateinit var etUsername: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        tvLoggedIn = findViewById<TextView>(R.id.tvLoggedIn)
        ivProfilePicture = findViewById<ImageView>(R.id.ivProfilePicture)
        etUsername = findViewById<EditText>(R.id.etUsername)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnUpdateProfile = findViewById<Button>(R.id.btnUpdateProfile)
        btnRegister.setOnClickListener {
            registerUser()
        }
        btnLogin.setOnClickListener {
            loginUser()
        }
        btnUpdateProfile.setOnClickListener {
            updateUserNameAndProfilePicture()
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
    private fun updateUserNameAndProfilePicture(){
        auth.currentUser?.let{
            val profileUri = Uri.parse("android.resource://$packageName/${R.drawable.profile}")
            val profileUpdates = UserProfileChangeRequest.Builder().
                    setDisplayName(etUsername.text.toString()).setPhotoUri(profileUri).build()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    auth.currentUser!!.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,"Profile Updated",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,"Error - $e",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
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
                    Toast.makeText(this@MainActivity,"Logged In Successfully",Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Log.d("MainActivity","Exception - $e")
            }
        }
    }

    private fun checkLogInState() {
        val user = auth.currentUser
        if (user == null){
            tvLoggedIn.text = "You are not logged in"
        }else{
            tvLoggedIn.text = "You are logged in"
            etUsername.setText(user.displayName)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}
package com.angelo.testapplications.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.userprofile.view.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth:FirebaseAuth by lazy {FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goTo()
    }

    private fun goTo(){
        if(auth.currentUser != null){
            startActivity(Intent(this,
                UserProfileActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
    }

}

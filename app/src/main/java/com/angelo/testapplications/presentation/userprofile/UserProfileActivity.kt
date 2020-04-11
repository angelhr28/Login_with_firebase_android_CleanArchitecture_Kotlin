package com.angelo.testapplications.presentation.userprofile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btn_signOut.setOnClickListener {
            signOut()
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_user_profile
    }

    fun signOut(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Toast.makeText(this,"Chau", Toast.LENGTH_LONG).show()
            startActivity(
                Intent(this,
                    SignInActivity::class.java)
            )
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Ocurrió un error ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

}

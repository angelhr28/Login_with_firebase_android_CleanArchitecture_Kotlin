package com.angelo.testapplications.domain.interactors.signIn

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.angelo.testapplications.presentation.signin.exception.FirebaseSignInException
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.userprofile.UserProfileActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignInInteractorImpl:SignInInteractor{

    private val TAG = "GoogleUser"
    private val auth  by lazy {FirebaseAuth.getInstance()}

    override suspend fun signInWithEmailAndPassword(email: String, password: String):Unit = suspendCancellableCoroutine {continuation->
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {

            if(it.isSuccessful){
                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(FirebaseSignInException(it.exception?.message!!))
            }
        }
    }

    override suspend fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?):Unit = suspendCancellableCoroutine {continuation->
        if (requestCode == SignInActivity.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Log.d(TAG,"${user?.displayName}")
                continuation.resume(Unit)
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                continuation.resumeWithException(FirebaseSignInException(response?.error?.errorCode.toString()))
            }
        }
    }

}
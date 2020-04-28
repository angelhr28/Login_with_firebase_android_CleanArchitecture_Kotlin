package com.angelo.testapplications.domain.interactors.signIn

import android.content.Intent
import com.google.firebase.auth.AuthCredential

interface SignInInteractor {

    suspend fun signInWithEmailAndPassword(email:String,password:String)
    suspend fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?)
    suspend fun signInWithFacebookAccount(credential: AuthCredential)
}
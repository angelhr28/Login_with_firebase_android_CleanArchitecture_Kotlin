package com.angelo.testapplications.domain.interactors.signIn

import android.content.Intent

interface SignInInteractor {

    suspend fun signInWithEmailAndPassword(email:String,password:String)
    suspend fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?)
}
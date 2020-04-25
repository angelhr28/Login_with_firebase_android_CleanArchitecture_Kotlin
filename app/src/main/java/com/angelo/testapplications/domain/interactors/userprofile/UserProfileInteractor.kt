package com.angelo.testapplications.domain.interactors.userprofile

import android.content.Context
import com.angelo.testapplications.presentation.userprofile.model.User
import com.google.firebase.auth.FirebaseUser

interface UserProfileInteractor {


    suspend fun signOut(context: Context)
}
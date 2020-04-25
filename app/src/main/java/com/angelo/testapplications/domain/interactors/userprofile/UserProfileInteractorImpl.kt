package com.angelo.testapplications.domain.interactors.userprofile

import android.content.Context
import android.util.Log
import com.angelo.testapplications.presentation.userprofile.exception.FirebaseUserProfileException
import com.angelo.testapplications.presentation.userprofile.model.User
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


class UserProfileInteractorImpl:UserProfileInteractor {

    private val authUI:AuthUI by lazy { AuthUI.getInstance() }

    override suspend fun signOut(context: Context):Unit = suspendCancellableCoroutine {continuation->

        authUI.signOut(context).addOnCompleteListener {
            if(it.isSuccessful){

                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(
                    FirebaseUserProfileException(
                        it.exception?.message!!
                    )
                )
            }
        }

    }

}
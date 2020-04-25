package com.angelo.testapplications.domain.interactors.recoverpassword

import com.angelo.testapplications.presentation.recoverpassword.exception.FirebaseRecoverPasswordException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RecoverPasswordInteractorImpl:RecoverPasswordInteractor {

    override suspend fun recoverPassword(email: String):Unit = suspendCancellableCoroutine {continuation->
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(FirebaseRecoverPasswordException(it.exception?.message!!))
            }
        }
    }
}


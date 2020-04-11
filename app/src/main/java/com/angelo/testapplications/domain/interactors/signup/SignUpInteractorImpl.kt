package com.angelo.testapplications.domain.interactors.signup

import android.util.Log
import com.angelo.testapplications.presentation.signup.exception.FirebaseSignUpException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignUpInteractorImpl:SignUpInteractor {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val dbReference by lazy { FirebaseDatabase.getInstance() }
    private val TAG = "VerifyEmail"

    override suspend fun createUserWithEmailAndPassword(
        fullname: String,
        email: String,
        password: String
    ):Unit = suspendCancellableCoroutine {continuation->
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

            if(it.isSuccessful){
                val user:FirebaseUser? = auth.currentUser
                //verifyEmail(user)
                    val userBD: HashMap<String,String> = HashMap()
                    userBD["nombre"] = fullname
                    userBD["email"] = email
                    userBD["password"] = password
                    dbReference.reference.child("User").child(auth.currentUser?.uid!!).updateChildren(
                        userBD as Map<String, String>
                    )

                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(FirebaseSignUpException(it.exception?.message!!))
            }
        }
    }


    /*fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful){
                Log.d(TAG,"Mensaje de verificación enviado")
                Log.d(TAG,user.email!!)
                auth.signOut()
            }
            else Log.d(TAG,"Error de autenticación")
        }
    }*/

}
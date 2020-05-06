package com.angelo.testapplications.domain.interactors.signIn

import android.content.Intent
import android.util.Log
import com.angelo.testapplications.presentation.signin.exception.FirebaseSignInException
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignInInteractorImpl:SignInInteractor{

    private val auth  by lazy {FirebaseAuth.getInstance()}
    private val dbReference by lazy{FirebaseDatabase.getInstance()}

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
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SignInActivity.RC_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userGoogle = auth.currentUser
                            createUserIntoFirebase(userGoogle)
                            continuation.resume(Unit)
                        } else {
                            // If sign in fails, display a message to the user.
                            continuation.resumeWithException(FirebaseSignInException("signInWithCredential:failure, error: ".plus(task.exception?.message)))
                            Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                        }
                    }


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                continuation.resumeWithException(FirebaseSignInException("Google sign in failed, error: ".plus(e.message)))
                Log.w("GoogleSignIn", "Google sign in failed", e)
            }
        }
    }

    override suspend fun signInWithFacebookAccount(credential:AuthCredential):Unit = suspendCancellableCoroutine{continuation->
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                val fbUser = auth.currentUser
                createUserIntoFirebase(fbUser)
                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(FirebaseSignInException(it.exception?.message!!))
            }
        }
    }


    fun createUserIntoFirebase(user: FirebaseUser?){
        val userBD: HashMap<String,String> = HashMap()
        userBD["name"] = user?.displayName.toString()
        userBD["email"] = user?.email.toString()
        userBD["image"] = user?.photoUrl.toString()
        dbReference.reference.child("User").child(user?.uid!!).updateChildren(
            userBD as Map<String, String>)
    }


}
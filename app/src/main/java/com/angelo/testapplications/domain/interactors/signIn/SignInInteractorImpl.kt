package com.angelo.testapplications.domain.interactors.signIn

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.angelo.testapplications.presentation.signin.exception.FirebaseSignInException
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.userprofile.view.UserProfileActivity
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignInInteractorImpl:SignInInteractor{

    private val TAG = "GoogleUser"
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
                //firebaseAuthWithGoogle(account!!)

                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userGoogle = auth.currentUser
                            val userBD: HashMap<String,String> = HashMap()
                            //Log.d(TAG,downloadImage.toString())
                            userBD["name"] = userGoogle?.displayName.toString()
                            userBD["email"] = userGoogle?.email.toString()
                            userBD["image"] = userGoogle?.photoUrl.toString()
                            dbReference.reference.child("User").child(userGoogle?.uid!!).updateChildren(
                                userBD as Map<String, String>)
                            // Sign in success, update UI with the signed-in user's information
                            continuation.resume(Unit)
                            /*Log.d("GoogleSignIn", "signInWithCredential:success")
                            val user = auth.currentUser
                            Log.d("GoogleSignIn",user?.displayName.toString())*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                        }
                    }


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                continuation.resumeWithException(FirebaseSignInException(e.message.toString()))
                Log.w("GoogleSignIn", "Google sign in failed", e)
                // ...
            }
        }
    }


    /*override suspend fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?):Unit = suspendCancellableCoroutine {continuation->
        if (requestCode == SignInActivity.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)


            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = auth.currentUser

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
    }*/

}
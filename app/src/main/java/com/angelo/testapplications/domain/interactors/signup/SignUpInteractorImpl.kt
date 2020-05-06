package com.angelo.testapplications.domain.interactors.signup

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.angelo.testapplications.presentation.signup.exception.FirebaseSignUpException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignUpInteractorImpl:SignUpInteractor {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val dbReference by lazy { FirebaseDatabase.getInstance() }
    private val storageReference:StorageReference by lazy { FirebaseStorage.getInstance().reference }
    private val TAG = "Verify"

    override suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
        image:Bitmap?
    ):Unit = suspendCancellableCoroutine {continuation->
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

               if(it.isSuccessful){
                   if(image != null) {
                       val fotoRef: StorageReference = storageReference.child("Fotos")
                           .child(auth.currentUser?.uid!!)
                           .child("imagen")

                       val baos = ByteArrayOutputStream()
                       image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                       val data = baos.toByteArray()
                       //fotoRef.putFile(uri)

                       fotoRef.putBytes(data).continueWithTask { task ->
                           if (!task.isSuccessful) {
                               task.exception?.let { exception ->
                                   throw exception
                               }
                           }
                           fotoRef.downloadUrl

                       }.addOnCompleteListener { taskImage ->
                           if (taskImage.isSuccessful) {
                               val downloadImage = taskImage.result
                               val user: FirebaseUser? = auth.currentUser

                               val profileUpdate: UserProfileChangeRequest =
                                   UserProfileChangeRequest.Builder()
                                       .setDisplayName(name)
                                       .setPhotoUri(downloadImage)
                                       .build()
                               user?.updateProfile(profileUpdate)
                                   ?.addOnCompleteListener { updateProfileTask ->
                                       if (updateProfileTask.isSuccessful) {
                                           createUserIntoFirebase(user,name,email,downloadImage.toString())
                                           verifyEmail(user)
                                       }
                                   }
                           }
                       }
                   }
                   continuation.resume(Unit)
            }else{
                Log.d(TAG,"No se creo el usuario")
                continuation.resumeWithException(FirebaseSignUpException(it.exception?.message!!))
            }

        }
    }


    fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful){
                Log.d(TAG,"Mensaje de verificación enviado")
                Log.d(TAG,user.email!!)
            }
            else Log.d(TAG,"Error de autenticación")
        }
    }

    fun createUserIntoFirebase(user:FirebaseUser?,name:String,email: String,image: String){
        val userBD: HashMap<String, String> = HashMap()
        userBD["name"] = name
        userBD["email"] = email
        userBD["image"] = image
        dbReference.reference.child("User").child(user?.uid!!)
            .updateChildren(
                userBD as Map<String, String>
            )
    }

}



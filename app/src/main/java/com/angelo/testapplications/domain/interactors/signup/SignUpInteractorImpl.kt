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
        filePath:Uri?
    ):Unit = suspendCancellableCoroutine {continuation->
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

               if(it.isSuccessful){
                   if(filePath != null){
                       val fotoRef:StorageReference = storageReference.child("Fotos")
                                                        .child(auth.currentUser?.uid!!)
                               //lastPathSegment obtiene el nombre del archivo de la imagen seleccionada por el usuario
                               //lastpathSegment obtiene un valor aleatorio como nombre de la imagen seleccionada
                                                        .child(filePath.lastPathSegment!!)

                       /*val bitMapImage:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                       val bitMap = getResizedBitmap(bitMapImage,1024)

                        val baos = ByteArrayOutputStream()
                        val data = baos.toByteArray()
                        val uploadTask = storageReference.putBytes(data)*/

                       //fotoRef.putFile(filePath)
                       fotoRef.putFile(filePath).continueWithTask<Uri>{task->
                           if (!task.isSuccessful) {
                               task.exception?.let {exception->
                                   throw exception
                               }
                           }
                           fotoRef.downloadUrl

                       }.addOnCompleteListener {taskImage->
                           if(taskImage.isSuccessful){
                               val downloadImage = taskImage.result
                               val user:FirebaseUser? = auth.currentUser

                               val profileUpdate: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                                   .setDisplayName(name)
                                   .setPhotoUri(downloadImage)
                                   .build()
                               user?.updateProfile(profileUpdate)?.addOnCompleteListener {updateProfileTask->

                                   if(updateProfileTask.isSuccessful){
                                       val userBD: HashMap<String,String> = HashMap()
                                       //Log.d(TAG,downloadImage.toString())
                                       userBD["name"] = name
                                       userBD["email"] = email
                                       userBD["password"] = password
                                       userBD["image"] = downloadImage.toString()
                                       dbReference.reference.child("User").child(user.uid).updateChildren(
                                           userBD as Map<String, String>)
                                       verifyEmail(user)
                                   }

                               }
                               //continuation.resume(Unit)
                           }
                       }
                   }

                   continuation.resume(Unit)

            }else{
                Log.d(TAG,"No se creo con exito")
                continuation.resumeWithException(FirebaseSignUpException(it.exception?.message!!))
            }

        }
    }


    fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful){
                Log.d(TAG,"Mensaje de verificación enviado")
                Log.d(TAG,user.email!!)
                //auth.signOut()
            }
            else Log.d(TAG,"Error de autenticación")
        }
    }

    /*fun sendDatestoFirebase(user:FirebaseUser,name:String,email:String,password: String,downloadImage:Uri?){

    }*/



    /*fun getResizedBitmap(bitMapImage:Bitmap, maxSize:Int):Bitmap{

        var width = bitMapImage.width
        var height = bitMapImage.height

        if(width<=maxSize && height <= maxSize){
            return bitMapImage
        }

        val bitMapRatio = width.toFloat() / height.toFloat()

        if(bitMapRatio>1){
            width = maxSize
            height = (width/bitMapRatio).toInt()
        }else{
            height = maxSize
            width = (height/bitMapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitMapImage,width,height,true)

    }*/

}



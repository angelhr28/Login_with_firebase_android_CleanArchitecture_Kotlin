package com.angelo.testapplications.domain.interactors.signup


import android.graphics.ImageDecoder
import android.net.Uri

interface SignUpInteractor {

    suspend fun createUserWithEmailAndPassword(name:String,email:String,password:String,filePath:Uri?)

}
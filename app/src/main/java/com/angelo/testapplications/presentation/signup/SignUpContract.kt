package com.angelo.testapplications.presentation.signup


import android.graphics.ImageDecoder
import android.net.Uri

interface SignUpContract {

    interface SignUpView{

        fun showError(msgError:String)
        fun showProgressBar()
        fun hideProgressBar()

        fun openGallery()
        fun showGallery()
        fun signUp()

        fun navigateToUserProfile()
    }

    interface SignUpPresenter{

        fun attachView(view:SignUpView)
        fun detachView()
        fun detachJob()
        fun isViewAttached():Boolean
        fun checkEmptyName(fullname: String):Boolean
        fun checkEmptyEmail(email: String):Boolean
        fun checkValidEmail(email: String):Boolean
        fun checkEmptyPassword(password: String):Boolean
        fun checkPasswordMatch(pw1: String,pw2:String):Boolean
        fun checkImage(uri: Uri?):Boolean

        fun checkRequestPermission(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray)


        fun signUp(name:String,email:String,password:String,filePath:Uri?)

    }

}
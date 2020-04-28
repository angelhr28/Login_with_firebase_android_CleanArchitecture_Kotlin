package com.angelo.testapplications.presentation.signin

import android.content.Intent
import com.google.firebase.auth.AuthCredential

interface SignInContract {

    interface SignInView{

        fun showError(msgError:String)
        fun showProgressBar()
        fun hideProgressBar()

        fun signIn()
        fun signInWithGoogleAccount()


        fun navigateToRegister()
        fun navigateToUserProfile()
        fun navigateToRecoverPassword()

    }

    interface SignInPresenter{

        fun attachView(view:SignInView)
        fun detachView()
        fun detachJob()
        fun isViewAttached():Boolean
        fun checkEmptyEmail(email:String):Boolean
        fun checkValidEmail(email:String):Boolean
        fun checkEmptyPassword(password:String):Boolean

        fun signInWithEmailAndPassword(email:String,password:String)
        fun signInWithGoogleAccount(requestCode:Int,resultCode:Int,data:Intent?)
        fun signInWithFacebookAccount(credential: AuthCredential)


    }


}
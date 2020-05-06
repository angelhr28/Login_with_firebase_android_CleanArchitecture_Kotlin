package com.angelo.testapplications.presentation.signup.presenter


import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.angelo.testapplications.domain.interactors.signup.SignUpInteractor
import com.angelo.testapplications.presentation.signup.SignUpContract
import com.angelo.testapplications.presentation.signup.exception.FirebaseSignUpException
import com.angelo.testapplications.presentation.signup.view.SignUpActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignUpPresenter(val signUpInteractor:SignUpInteractor):SignUpContract.SignUpPresenter,CoroutineScope {

    private var view:SignUpContract.SignUpView? = null

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun attachView(view: SignUpContract.SignUpView) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun detachJob() {
        coroutineContext.cancel()
    }

    override fun isViewAttached(): Boolean {
        return view != null
    }

    override fun checkEmptyName(fullname: String):Boolean {
        return TextUtils.isEmpty(fullname)
    }

    override fun checkEmptyEmail(email: String):Boolean {
        return TextUtils.isEmpty(email)
    }

    override fun checkValidEmail(email: String):Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun checkEmptyPassword(password: String):Boolean {
        return TextUtils.isEmpty(password)
    }

    override fun checkPasswordMatch(pw1: String, pw2: String):Boolean {
        return pw1 == pw2
    }

    override fun checkImage(uri: Uri?): Boolean {
        return uri != null
    }

    override fun checkRequestPermission(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            SignUpActivity.REQUEST_GALLERY_CODE ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    view?.showGallery()
                }
            }
        }
    }


    override fun signUp(name: String, email: String, password: String, image:Bitmap?) {
        launch {
            view?.showProgressBar()
            try {
                signUpInteractor.createUserWithEmailAndPassword(name,email,password,image!!)
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.navigateToUserProfile()
                }
            }catch (e:FirebaseSignUpException){
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.showError(e.message!!)
                }
            }
        }
    }



}
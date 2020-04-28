package com.angelo.testapplications.presentation.signin.presenter

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.core.util.PatternsCompat
import com.angelo.testapplications.R
import com.angelo.testapplications.domain.interactors.signIn.SignInInteractor
import com.angelo.testapplications.presentation.signin.exception.FirebaseSignInException
import com.angelo.testapplications.presentation.signin.SignInContract
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInPresenter(val signInInteractor:SignInInteractor):SignInContract.SignInPresenter,CoroutineScope {

    //Google
    private lateinit var signInClient: GoogleSignInClient

    private var view:SignInContract.SignInView?=null


    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun attachView(view: SignInContract.SignInView) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun detachJob() {
        coroutineContext.cancel()
    }

    override fun isViewAttached():Boolean {
        return view != null
    }

    override fun checkEmptyEmail(email: String): Boolean {
        return TextUtils.isEmpty(email)
    }

    override fun checkValidEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun checkEmptyPassword(password: String): Boolean {
        return TextUtils.isEmpty(password)
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {

        launch {
            view?.showProgressBar()
            try {
                signInInteractor.signInWithEmailAndPassword(email,password)
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.navigateToUserProfile()
                }
            }catch (e:FirebaseSignInException){
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.showError(e.message!!)
                }
            }
        }
    }


    override fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?){
        launch{
            try {
                signInInteractor.signInWithGoogleAccount(requestCode,resultCode,data)
                if(isViewAttached()){
                    view?.navigateToUserProfile()
                }
            }catch(e:FirebaseSignInException){
                if (isViewAttached()){
                    view?.showError(e.message!!)
                }
            }
        }
    }


    override fun signInWithFacebookAccount(credential:AuthCredential){
        launch {
            try{
                signInInteractor.signInWithFacebookAccount(credential)
                if(isViewAttached()){
                    view?.navigateToUserProfile()
                }
            }catch(e:FirebaseSignInException){
                if(isViewAttached()){
                    view?.showError(e.message!!)
                }
            }
        }
    }



}
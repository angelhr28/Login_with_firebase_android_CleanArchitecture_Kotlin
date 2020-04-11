package com.angelo.testapplications.presentation.signin.presenter

import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.angelo.testapplications.domain.interactors.signIn.SignInInteractor
import com.angelo.testapplications.presentation.signin.exception.FirebaseSignInException
import com.angelo.testapplications.presentation.signin.SignInContract
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SignInPresenter(val signInInteractor:SignInInteractor):SignInContract.SignInPresenter,CoroutineScope {

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

    override fun signInWithGoogleAccount(requestCode: Int, resultCode: Int, data: Intent?) {
        launch {
            try {
                signInInteractor.signInWithGoogleAccount(requestCode,resultCode,data)
                if(isViewAttached()){
                    view?.navigateToUserProfile()
                }
            }catch (e:FirebaseSignInException){
                if(isViewAttached()){
                    view?.showError(e.message!!)
                }
            }
        }
    }

    /*override fun signInWithGoogleAccount() {

            try {
                signInInteractor.signInWithGoogleAccount()
            }catch(e:FirebaseSignInException){
                view?.showError(e.message!!)
            }


    }*/



}
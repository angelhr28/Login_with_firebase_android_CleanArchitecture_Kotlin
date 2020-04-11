package com.angelo.testapplications.presentation.signup.presenter


import android.text.TextUtils
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.angelo.testapplications.domain.interactors.signup.SignUpInteractor
import com.angelo.testapplications.presentation.signup.SignUpContract
import com.angelo.testapplications.presentation.signup.exception.FirebaseSignUpException
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

    override fun signUp(fullname: String, email: String, password: String) {
        launch {
            view?.showProgressBar()
            try {
                signUpInteractor.createUserWithEmailAndPassword(fullname,email,password)
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
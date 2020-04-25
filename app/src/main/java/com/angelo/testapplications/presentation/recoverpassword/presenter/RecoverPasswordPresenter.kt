package com.angelo.testapplications.presentation.recoverpassword.presenter

import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.util.PatternsCompat
import com.angelo.testapplications.domain.interactors.recoverpassword.RecoverPasswordInteractor
import com.angelo.testapplications.presentation.recoverpassword.RecoverPasswordContract
import com.angelo.testapplications.presentation.recoverpassword.exception.FirebaseRecoverPasswordException
import com.angelo.testapplications.presentation.recoverpassword.view.RecoverPasswordActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RecoverPasswordPresenter(private val recoverPasswordInteractor:RecoverPasswordInteractor):RecoverPasswordContract.RecoverPasswordPresenter,CoroutineScope {

    private var view:RecoverPasswordContract.RecoverPasswordView? = null

    val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun attachView(view: RecoverPasswordContract.RecoverPasswordView) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun detachJob(){
        coroutineContext.cancel()
    }

    override fun isViewAttached(): Boolean {
        return view != null
    }

    override fun checkEmptyEmail(email: String): Boolean {
        return TextUtils.isEmpty(email)
    }

    override fun checkValidEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun recoverPassword(email: String) {
        launch {
            view?.showProgressBar()
            try {
                recoverPasswordInteractor.recoverPassword(email)
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.navigateToLogin()
                }
            }catch (e:FirebaseRecoverPasswordException){
                    view?.hideProgressBar()
                    view?.showError(e.message!!)
            }
        }
    }
}
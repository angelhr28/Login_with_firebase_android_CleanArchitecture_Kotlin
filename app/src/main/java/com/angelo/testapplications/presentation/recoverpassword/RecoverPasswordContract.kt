package com.angelo.testapplications.presentation.recoverpassword

interface RecoverPasswordContract {

    interface RecoverPasswordView{
        fun showError(msgError:String)
        fun showProgressBar()
        fun hideProgressBar()
        fun recoverPassword()

        fun navigateToLogin()

    }

    interface RecoverPasswordPresenter{
        fun attachView(view:RecoverPasswordView)
        fun detachView()
        fun detachJob()
        fun isViewAttached():Boolean
        fun checkEmptyEmail(email:String):Boolean
        fun checkValidEmail(email:String):Boolean

        fun recoverPassword(email:String)
    }


}
package com.angelo.testapplications.presentation.signup

interface SignUpContract {

    interface SignUpView{

        fun showError(msgError:String)
        fun showProgressBar()
        fun hideProgressBar()

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
        fun signUp(fullname:String,email:String,password:String)

    }

}
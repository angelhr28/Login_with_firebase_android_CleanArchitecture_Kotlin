package com.angelo.testapplications.presentation.userprofile

import android.content.Context
import com.angelo.testapplications.presentation.userprofile.model.User
import com.google.firebase.auth.FirebaseUser

interface UserProfileContract {

    interface UserProfileView{
        fun showError(msgError:String)
        fun showProgressBar()
        fun hideProgressBar()

        fun setUserFromFirebase(user:User?)
        fun signOut()

        fun navigateToSignIn()

    }

    interface UserProfilePresenter{
        fun attachView(view:UserProfileView)
        fun detachView()
        fun detachJob()
        fun isViewAttached():Boolean

        fun getUserFromFirebase()
        fun signOut(context:Context)

        fun onStart()
        fun onStop()
    }

}
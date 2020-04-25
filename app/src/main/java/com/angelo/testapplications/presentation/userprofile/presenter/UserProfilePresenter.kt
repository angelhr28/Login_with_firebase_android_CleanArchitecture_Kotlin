package com.angelo.testapplications.presentation.userprofile.presenter

import android.content.Context
import com.angelo.testapplications.domain.interactors.userprofile.UserProfileInteractor
import com.angelo.testapplications.presentation.userprofile.UserProfileContract
import com.angelo.testapplications.presentation.userprofile.exception.FirebaseUserProfileException
import com.angelo.testapplications.presentation.userprofile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class UserProfilePresenter(val userProfileInteractor:UserProfileInteractor):UserProfileContract.UserProfilePresenter,CoroutineScope {

    private var view:UserProfileContract.UserProfileView? = null
    private val job = Job()

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val dbReference: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private lateinit var authListener:FirebaseAuth.AuthStateListener



    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun attachView(view: UserProfileContract.UserProfileView) {
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


    override fun getUserFromFirebase(){
        val userDB = auth.currentUser

        dbReference.reference.child("User").child(userDB?.uid!!).addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user:User? = dataSnapshot.getValue(User::class.java)
                view?.setUserFromFirebase(user)
            }

        })

    }

    override fun signOut(context: Context) {
        launch {
            view?.showProgressBar()
            try {
                userProfileInteractor.signOut(context)
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.navigateToSignIn()
                }
            }catch (e:FirebaseUserProfileException){
                if(isViewAttached()){
                    view?.hideProgressBar()
                    view?.showError(e.message!!)
                }

            }
        }
    }

    override fun onStart() {
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        auth.removeAuthStateListener(authListener)
    }


}
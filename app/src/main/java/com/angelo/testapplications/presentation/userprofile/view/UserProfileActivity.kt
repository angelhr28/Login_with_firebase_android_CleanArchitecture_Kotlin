package com.angelo.testapplications.presentation.userprofile.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.userprofile.UserProfileInteractorImpl
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.userprofile.UserProfileContract
import com.angelo.testapplications.presentation.userprofile.model.User
import com.angelo.testapplications.presentation.userprofile.presenter.UserProfilePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivity(),UserProfileContract.UserProfileView {

    private lateinit var presenter:UserProfilePresenter
    private lateinit var googleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = UserProfilePresenter(UserProfileInteractorImpl())
        presenter.attachView(this)
        presenter.getUserFromFirebase()

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        btn_signOut.setOnClickListener {
            signOut()
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_user_profile
    }

    override fun showError(msgError:String) {
        toast(this,msgError)
    }

    override fun showProgressBar() {
        btn_signOut.visibility = View.GONE
        progressBar_signOut.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        btn_signOut.visibility = View.VISIBLE
        progressBar_signOut.visibility = View.GONE
    }

    override fun setUserFromFirebase(user: User?) {
        getDates(user)
    }

    override fun signOut(){
        presenter.signOut(this)
        //Cerrar sesión cuando se logea con Google
        //Primero cerrar sesión en firebase y luego en google
        if(GoogleSignIn.getLastSignedInAccount(this) != null){
            signOutGoogle()
        }else if(isLoggedInWithFacebook()){
            logOutFacebook()
        }

    }

    //logout function to google
    fun signOutGoogle(){
        googleSignInClient?.signOut()?.addOnCompleteListener {
            toast(this,"Cerró sesión de Google")
        }
    }

    //logout function to facebook
    fun isLoggedInWithFacebook():Boolean{
        return AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null
    }

    fun logOutFacebook(){
        toast(this,"Cerró sesión de Facebook")
        LoginManager.getInstance().logOut()
    }

    override fun navigateToSignIn() {
        val intent = Intent(this,SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun getDates(user:User?){
        Glide
            .with(this)
            .load(user?.image)
            .centerCrop()
            .listener(object: RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar_uploadImage.visibility = View.GONE
                    iV_photo_user_profile.visibility = View.VISIBLE
                    iV_photo_user_profile.setImageResource(R.drawable.ic_error_black_24dp)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar_uploadImage.visibility = View.GONE
                    iV_photo_user_profile.visibility = View.VISIBLE
                    return false
                }

            })
            .into(iV_photo_user_profile)

        txt_name_user_profile.text = user?.name.toString()
        txt_email_user_profile.text = user?.email.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }

}

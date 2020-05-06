package com.angelo.testapplications.presentation.signin.view

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.text.HtmlCompat
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.signIn.SignInInteractorImpl
import com.angelo.testapplications.presentation.recoverpassword.view.RecoverPasswordActivity
import com.angelo.testapplications.presentation.signin.SignInContract
import com.angelo.testapplications.presentation.signin.presenter.SignInPresenter
import com.angelo.testapplications.presentation.signup.view.SignUpActivity
import com.angelo.testapplications.presentation.userprofile.view.UserProfileActivity
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_inn.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.Arrays.asList


class SignInActivity : BaseActivity(),SignInContract.SignInView {

    //Presenter
    private lateinit var presenter: SignInPresenter

    //facebook
    private lateinit var callbackManager: CallbackManager
    //Google
    private lateinit var signInClient: GoogleSignInClient

    companion object{
        const val RC_SIGN_IN_GOOGLE = 9001
        const val RC_SIGN_IN_FACEBOOK = 64206
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SignInPresenter(SignInInteractorImpl())
        presenter.attachView(this)

        //Sign in with Email and Password
        btn_signIn.setOnClickListener {
           signIn()
        }

        txt_register.setOnClickListener {
            navigateToRegister()
        }

        txt_recover_password.setOnClickListener {
            navigateToRecoverPassword()
        }

        // Configure Google Client
        configureGoogleClient()

        btnSignInGoogle.setOnClickListener {
            changeClick(btnSignInGoogle)
            signInWithGoogleAccount()
        }

        //Configure Facebook Login
        configureFacebookLogin()

        btnSignInFacebook.setOnClickListener {
            changeClick(btnSignInFacebook)
            signInWithFacebookAccount()
        }

    }



    override fun getLayout(): Int {
        return R.layout.activity_sign_inn
    }

    override fun showError(msgError: String) {
        toast(this,msgError)
    }

    override fun showProgressBar() {
        btn_signIn.visibility = View.GONE
        progressBar_signIn.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar_signIn.visibility = View.GONE
        btn_signIn.visibility = View.VISIBLE
    }


    //--------------Sign in With EmailAndPassword-----------------------
    override fun signIn() {
        val email = etxt_email.text.toString().trim()
        val password = etxt_password.text.toString().trim()

        if(presenter.checkEmptyEmail(email)){
            etxt_email.error = "Enter an e-mail, please."
            return
        }

        if(!presenter.checkValidEmail(email)){
            etxt_email.error = "The e-mail is invalid."
            return
        }

        if(presenter.checkEmptyPassword(password)){
            etxt_password.error = "Enter an password, please."
            return
        }

        presenter.signInWithEmailAndPassword(email,password)
    }


    //--------------Sign in With Google-----------------------
    fun configureGoogleClient(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this,gso)
    }

    override fun signInWithGoogleAccount() {
        val intent:Intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE)
    }


    //--------------Sign in With Facebook-----------------------
    fun configureFacebookLogin(){
        callbackManager = CallbackManager.Factory.create()
        btn_login_fb.setReadPermissions("email","public_profile")
    }

    private fun signInWithFacebookAccount() {
        btn_login_fb.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                val accessToken = result!!.accessToken
                val credential = FacebookAuthProvider.getCredential(accessToken.token)
                presenter.signInWithFacebookAccount(credential)
            }

            override fun onCancel() {
                toast(applicationContext,"Login Canceled")
            }

            override fun onError(error: FacebookException?) {
                toast(applicationContext,"Error Login: "+error?.message.toString())
            }
        })
    }

    //--------------ActivityResults-----------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN_GOOGLE ->{
                Log.d("RC_SIGN_IN_GOOGLE",requestCode.toString())
                presenter.signInWithGoogleAccount(requestCode,resultCode,data)
            }
            RC_SIGN_IN_FACEBOOK->{
                Log.d("RC_SIGN_IN_FACEBOOK",requestCode.toString())
                callbackManager.onActivityResult(requestCode,resultCode,data)
            }
        }
    }


    //-------------- NAVIGATES -----------------------
    override fun navigateToRegister() {
        startActivity(Intent(this, SignUpActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    override fun navigateToRecoverPassword() {
        startActivity(Intent(this, RecoverPasswordActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    override fun navigateToUserProfile() {
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    private fun changeClick(v:View){
        when(v.id){
            R.id.btnSignInFacebook->btn_login_fb.performClick()
            R.id.btnSignInGoogle->btn_login_goo.performClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }

}

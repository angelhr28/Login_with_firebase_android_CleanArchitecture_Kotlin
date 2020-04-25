package com.angelo.testapplications.presentation.signin.view

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : BaseActivity(),SignInContract.SignInView {

    private lateinit var presenter: SignInPresenter
    private lateinit var signInClient: GoogleSignInClient
    private val auth:FirebaseAuth by lazy{FirebaseAuth.getInstance()}
    private val dbReference by lazy{FirebaseDatabase.getInstance()}
    private lateinit var callbackManager:CallbackManager
    private lateinit var facebookLoginManager:LoginManager

    companion object{
        const val RC_SIGN_IN = 1001
        const val RC_SIGN_IN_GOOGLE = 9001
        const val RC_SIGN_IN_FACEBOOK = 64206
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SignInPresenter(SignInInteractorImpl())
        presenter.attachView(this)


        btn_signIn_asd.setOnClickListener {
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

        btn_login_goo.setOnClickListener {
            signInWithGoogleAccount()
        }

        //Configure Facebook Login
        configureFacebookLogin()

        btn_login_fb.setOnClickListener {
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
        btn_signIn_asd.visibility = View.GONE
        progressBar_signIn.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar_signIn.visibility = View.GONE
        btn_signIn_asd.visibility = View.VISIBLE
    }

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



    //Sign in With Google

    fun configureGoogleClient(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //for the requestIdToken, this is in the values.xml file that
            //is generated from your google-services.json
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        signInClient = GoogleSignIn.getClient(this,gso)

        // Set the dimensions of the sign-in button.
        btn_login_goo.setSize(SignInButton.SIZE_ICON_ONLY)

    }

    override fun signInWithGoogleAccount() {
        val intent:Intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN_GOOGLE)
    }

    fun configureFacebookLogin(){
        callbackManager = CallbackManager.Factory.create()
        btn_login_fb.setReadPermissions("email","public_profile")
    }

    private fun signInWithFacebookAccount() {
        btn_login_fb.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                toast(applicationContext,"Cancel Login")
            }

            override fun onError(error: FacebookException?) {
                toast(applicationContext,"Error Login: "+error?.message.toString())
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful){
                toast(this,"Login Facebook con éxito")
                val userFacebook = auth.currentUser
                val userBD: HashMap<String,String> = HashMap()
                //Log.d(TAG,downloadImage.toString())
                userBD["name"] = userFacebook?.displayName.toString()
                userBD["email"] = userFacebook?.email.toString()
                userBD["image"] = userFacebook?.photoUrl.toString()
                dbReference.reference.child("User").child(userFacebook?.uid!!).updateChildren(
                    userBD as Map<String, String>)
                navigateToUserProfile()
            }else{
                toast(this,"Login Facebook Falló")
            }
        }
    }


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

    override fun navigateToRegister() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    override fun navigateToRecoverPassword() {
        startActivity(Intent(this, RecoverPasswordActivity::class.java))
    }

    override fun navigateToUserProfile() {
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }

}

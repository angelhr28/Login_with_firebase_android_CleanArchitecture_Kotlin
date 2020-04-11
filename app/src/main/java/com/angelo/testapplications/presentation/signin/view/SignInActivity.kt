package com.angelo.testapplications.presentation.signin.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.signIn.SignInInteractorImpl
import com.angelo.testapplications.presentation.signup.view.SignUpActivity
import com.angelo.testapplications.presentation.signin.SignInContract
import com.angelo.testapplications.presentation.signin.presenter.SignInPresenter
import com.angelo.testapplications.presentation.userprofile.UserProfileActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ui.email.RecoverPasswordActivity
import kotlinx.android.synthetic.main.activity_sign_inn.*

class SignInActivity : BaseActivity(),SignInContract.SignInView {

    lateinit var presenter: SignInPresenter

    companion object{
        const val RC_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SignInPresenter(SignInInteractorImpl())
        presenter.attachView(this)

        btn_signIn_asd.setOnClickListener {
            signIn()
        }

        btn_login_goo.setOnClickListener {
            signInWithGoogleAccount()
        }

        txt_register.setOnClickListener {
            navigateToRegister()
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

    override fun signInWithGoogleAccount() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
            RC_SIGN_IN
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.signInWithGoogleAccount(requestCode,resultCode,data)
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    override fun navigateToRoverPassword() {
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

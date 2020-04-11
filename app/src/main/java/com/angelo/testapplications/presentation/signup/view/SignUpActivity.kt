package com.angelo.testapplications.presentation.signup.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.signup.SignUpInteractorImpl
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.signup.SignUpContract
import com.angelo.testapplications.presentation.signup.presenter.SignUpPresenter
import com.angelo.testapplications.presentation.userprofile.UserProfileActivity
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(),SignUpContract.SignUpView {

    lateinit var presenter:SignUpPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SignUpPresenter(SignUpInteractorImpl())
        presenter.attachView(this)

        btn_send_dates.setOnClickListener {
            signUp()
        }

    }

    override fun getLayout(): Int = R.layout.activity_sign_up

    override fun showError(msgError: String) {
        toast(this,msgError)
    }

    override fun showProgressBar() {
        btn_send_dates.visibility = View.GONE
        progressBar_signUp.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        btn_send_dates.visibility = View.VISIBLE
        progressBar_signUp.visibility = View.GONE
    }


    override fun signUp() {

        val fullname = etxt_name_signUp.text.toString().trim()
        val email = etxt_email_signUp.text.toString().trim()
        val password = etxt_password_signUp.text.toString().trim()
        val confirmPassword = etxt_confirm_password_signUp.text.toString().trim()

        if (presenter.checkEmptyName(fullname)){
            etxt_name_signUp.error = "Enter an name, please"
            return
        }
        if (presenter.checkEmptyEmail(email)){
            etxt_email_signUp.error = "Enter an e-mail, please"
            return
        }
        if (!presenter.checkValidEmail(email)){
            etxt_email_signUp.error = "The e-mail is invalid."
            return
        }
        if (presenter.checkEmptyPassword(password)){
            etxt_password_signUp.error = "Enter an password, please"
            return
        }
        if (!presenter.checkPasswordMatch(password,confirmPassword)){
            etxt_confirm_password_signUp.error = "The passwords do not match"
            return
        }

        presenter.signUp(fullname,email,confirmPassword)

    }

    override fun navigateToUserProfile() {
        val intent = Intent(this,SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }


}

package com.angelo.testapplications.presentation.recoverpassword.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.recoverpassword.RecoverPasswordInteractorImpl
import com.angelo.testapplications.presentation.recoverpassword.RecoverPasswordContract
import com.angelo.testapplications.presentation.recoverpassword.presenter.RecoverPasswordPresenter
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import kotlinx.android.synthetic.main.activity_recover_password.*

class RecoverPasswordActivity : BaseActivity(),RecoverPasswordContract.RecoverPasswordView {

    private lateinit var presenter:RecoverPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = RecoverPasswordPresenter(RecoverPasswordInteractorImpl())
        presenter.attachView(this)

        btn_send_email_to_recover_password.setOnClickListener {
            recoverPassword()
        }

    }

    override fun getLayout(): Int = R.layout.activity_recover_password

    override fun showError(msgError: String) {
        toast(this,msgError)
    }

    override fun showProgressBar() {
        progressBar_RecoverPassword.visibility = View.VISIBLE
        btn_send_email_to_recover_password.visibility = View.GONE
    }

    override fun hideProgressBar() {
        progressBar_RecoverPassword.visibility = View.GONE
        btn_send_email_to_recover_password.visibility = View.VISIBLE
    }

    override fun recoverPassword() {
        val email = etxt_email_recover_password.text.toString().trim()

        if(presenter.checkEmptyEmail(email)){
            etxt_email_recover_password.error = "The field is empty"
            return
        }

        if(!presenter.checkValidEmail(email)){
            etxt_email_recover_password.error = "The e-mail is invalid."
            return
        }

        presenter.recoverPassword(email)
    }

    override fun navigateToLogin() {
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

package com.angelo.testapplications.presentation.signup.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.angelo.testapplications.R
import com.angelo.testapplications.base.BaseActivity
import com.angelo.testapplications.domain.interactors.signup.SignUpInteractorImpl
import com.angelo.testapplications.presentation.signin.view.SignInActivity
import com.angelo.testapplications.presentation.signup.SignUpContract
import com.angelo.testapplications.presentation.signup.presenter.SignUpPresenter
import com.angelo.testapplications.presentation.userprofile.view.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream

class SignUpActivity : BaseActivity(),SignUpContract.SignUpView {

    lateinit var presenter:SignUpPresenter

    private var filePath:Uri? = null
    private val auth:FirebaseAuth by lazy {FirebaseAuth.getInstance()}

    companion object{
        const val REQUEST_GALLERY_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SignUpPresenter(SignUpInteractorImpl())
        presenter.attachView(this)

        btn_select_image.setOnClickListener {
            openGallery()
        }

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

    override fun openGallery(){
        //Verificando version de android
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissionGallery = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissionGallery,REQUEST_GALLERY_CODE)
            }else{
                showGallery()
            }
        }else{
            showGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.checkRequestPermission(requestCode, permissions, grantResults)
    }

    override fun showGallery(){
        val intentGallery = Intent(Intent.ACTION_PICK) // intent implicito
        intentGallery.type = "image/*"
        startActivityForResult(intentGallery,REQUEST_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_CODE && data != null){

           filePath = data.data

            val bitMapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,filePath!!))
            bitMapImage.compress(Bitmap.CompressFormat.JPEG,100,ByteArrayOutputStream())

            //val bitMap = getResizedBitmap(bitMapImage,1024)
            /*val bitMapImage:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
            getResizedBitmap(bitMapImage,1024)

            //Comprimimos la imagen a JPEG, tenemos la calidad al 100 y hacemos con baos que la imagen sea mas ligera
            val baos = ByteArrayOutputStream()
            bitMapImage.compress(Bitmap.CompressFormat.JPEG,100,baos)

            val data = baos.toByteArray()

            var uploadTask = FirebaseStorage.getInstance().reference.putBytes(data)*/

            //enviamos la imagen al imageView
            iV_Photo.setImageBitmap(bitMapImage)
        }
    }

    override fun signUp() {

        val name = etxt_name_signUp.text.toString().trim()
        val email = etxt_email_signUp.text.toString().trim()
        val password = etxt_password_signUp.text.toString().trim()
        val confirmPassword = etxt_confirm_password_signUp.text.toString().trim()
        val image = filePath

        checkEmptyFields(image,name,email,password, confirmPassword)

        presenter.signUp(name,email,confirmPassword,image)
    }

    override fun navigateToUserProfile() {
        val intent = Intent(this,UserProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun checkEmptyFields(image:Uri?,name:String,email:String,password:String,confirmPassword:String){
        if(!presenter.checkImage(image)){
            toast(this,"Select an image, please")
            return
        }

        if (presenter.checkEmptyName(name)){
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
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }




}

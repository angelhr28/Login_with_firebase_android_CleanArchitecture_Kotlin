package com.angelo.testapplications.domain.interactors.signup

interface SignUpInteractor {

    suspend fun createUserWithEmailAndPassword(fullname:String,email:String,password:String)

}
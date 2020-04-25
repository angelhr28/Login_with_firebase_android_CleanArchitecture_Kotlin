package com.angelo.testapplications.domain.interactors.recoverpassword

interface RecoverPasswordInteractor {

    suspend fun recoverPassword(email:String)

}
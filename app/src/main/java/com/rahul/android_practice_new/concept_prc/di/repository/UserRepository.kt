package com.rahul.android_practice_new.concept_prc.di.repository

import android.util.Log
import com.rahul.android_practice_new.concept_prc.di.LoggerService
import javax.inject.Inject

class UserRepository @Inject constructor(val loggerService: LoggerService) {
    fun saveUser(email: String, password: String){
        loggerService.log("email -> ${email}\n password -> ${password}")
    }
}
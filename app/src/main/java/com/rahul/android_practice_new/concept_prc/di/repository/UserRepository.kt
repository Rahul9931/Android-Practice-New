package com.rahul.android_practice_new.concept_prc.di.repository

import android.util.Log
import javax.inject.Inject

class UserRepository @Inject constructor() {
    fun saveUser(email: String, password: String){
        Log.d("check_userRepo", "email -> ${email}\n password -> ${password}")
    }
}
package com.rahul.android_practice_new.concept_prc.di

import android.util.Log
import javax.inject.Inject

class LoggerService @Inject constructor() {
    fun log(message: String){
        Log.d("check_userRepo", "${message}")
    }
}
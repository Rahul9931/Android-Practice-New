package com.rahul.android_practice_new.concept_prc.di.repository

import android.util.Log
import com.rahul.android_practice_new.concept_prc.di.LoggerService
import javax.inject.Inject


interface UserRepository {
    fun saveUser(email: String, password: String)
}
class SQLRepository @Inject constructor(val loggerService: LoggerService): UserRepository {
    override fun saveUser(email: String, password: String){
        loggerService.log("user saved in sql")
    }
}

class FirebaseRepository (): UserRepository {
    override fun saveUser(email: String, password: String){
        Log.d("check_firebase_repo","user saved in firebase")
    }
}
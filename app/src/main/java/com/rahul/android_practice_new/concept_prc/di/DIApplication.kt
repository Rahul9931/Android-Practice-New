package com.rahul.android_practice_new.concept_prc.di

import android.app.Application
import com.rahul.android_practice_new.concept_prc.di.repository.UserRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class DIApplication: Application() {

    @Inject
    @Named("sql")
    lateinit var userRepository: UserRepository
    override fun onCreate() {
        super.onCreate()
        userRepository.saveUser("rahul123@gmail.com", "234234")
    }
}
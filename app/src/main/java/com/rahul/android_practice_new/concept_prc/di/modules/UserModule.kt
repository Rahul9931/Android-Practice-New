package com.rahul.android_practice_new.concept_prc.di.modules

import com.rahul.android_practice_new.concept_prc.di.repository.FirebaseRepository
import com.rahul.android_practice_new.concept_prc.di.repository.SQLRepository
import com.rahul.android_practice_new.concept_prc.di.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class UserModule {

//    @Provides
//    fun provideFirebaseRepo(): UserRepository{
//        return FirebaseRepository()
//    }

    @Binds
    abstract fun provideSQLRepo(sqlRepository: SQLRepository): UserRepository
}
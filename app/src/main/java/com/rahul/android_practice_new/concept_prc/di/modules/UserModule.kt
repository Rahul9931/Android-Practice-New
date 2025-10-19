package com.rahul.android_practice_new.concept_prc.di.modules

import com.rahul.android_practice_new.concept_prc.di.FirebaseQualifier
import com.rahul.android_practice_new.concept_prc.di.repository.FirebaseRepository
import com.rahul.android_practice_new.concept_prc.di.repository.SQLRepository
import com.rahul.android_practice_new.concept_prc.di.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
class UserModule {

    @Provides
    @Named("sql")
    fun provideSqlRepo(sqlRepository: SQLRepository): UserRepository{
        return sqlRepository
    }

    @Provides
//    @Named("firebase")
    @FirebaseQualifier
    fun provideFirebaseRepo(): UserRepository{
        return FirebaseRepository()
    }

}
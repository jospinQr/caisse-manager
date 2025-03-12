package com.example.caissemanager.di

import android.app.Application
import android.content.Context
import com.example.caissemanager.data.repository.AuthRepository
import com.example.caissemanager.data.repository.CaisseRepository
import com.example.caissemanager.data.repository.CompteRepository
import com.example.caissemanager.data.repository.implementation.AuthRepoImp
import com.example.caissemanager.data.repository.implementation.CaisseRepositoryImpl
import com.example.caissemanager.data.repository.implementation.CompteRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.Identity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideCurrentUser(auth: FirebaseAuth): FirebaseUser? = auth.currentUser


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideAuthRepoImpl(auth: FirebaseAuth): AuthRepository = AuthRepoImp(auth)

    @Provides
    @Singleton
    fun provideCompteRepositoryImpl(db: FirebaseFirestore): CompteRepository =
        CompteRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideCaisseRepositoryImpl(db: FirebaseFirestore): CaisseRepository =
        CaisseRepositoryImpl(db)
}
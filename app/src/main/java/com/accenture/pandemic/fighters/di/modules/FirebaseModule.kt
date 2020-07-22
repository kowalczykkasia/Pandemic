package com.accenture.pandemic.fighters.di.modules

import com.accenture.pandemic.fighters.utils.FirebaseAuthentication
import com.accenture.pandemic.fighters.utils.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Singleton
    @Provides
    fun providesFirebase(): FirebaseAuthentication =
        FirebaseAuthentication(FirebaseAuth.getInstance())

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirebaseMessaging() : FirebaseMessaging = FirebaseMessaging.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage() : FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun provideStorage() : Storage = Storage(Firebase.storage)

}

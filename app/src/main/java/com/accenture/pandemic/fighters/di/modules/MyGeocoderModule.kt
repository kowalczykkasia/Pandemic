package com.accenture.pandemic.fighters.di.modules

import android.content.Context
import android.location.Geocoder
import com.accenture.pandemic.fighters.utils.MyGeocoder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MyGeocoderModule {
    @Singleton
    @Provides
    fun provideMyGeocoder(context :Context): MyGeocoder = MyGeocoder(Geocoder(context))


}
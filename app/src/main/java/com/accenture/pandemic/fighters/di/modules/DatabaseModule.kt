package com.accenture.pandemic.fighters.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context) : SharedPreferences =
        context.getSharedPreferences("pandemic_app", Context.MODE_PRIVATE)
}
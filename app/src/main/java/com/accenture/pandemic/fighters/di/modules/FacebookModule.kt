package com.accenture.pandemic.fighters.di.modules

import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FacebookModule {

    @Provides
    @Singleton
    fun providesCallbackManager() = CallbackManager.Factory.create()

    @Provides
    @Singleton
    fun providesLoginManager() = LoginManager.getInstance()

}
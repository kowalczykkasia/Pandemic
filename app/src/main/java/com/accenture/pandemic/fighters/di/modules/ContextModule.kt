package com.accenture.pandemic.fighters.di.modules

import android.content.Context
import com.accenture.pandemic.fighters.PandemicFighters
import dagger.Module
import dagger.Provides

@Module
class ContextModule {
    @Provides
    fun providesContext(application : PandemicFighters) :
            Context = application.applicationContext
}
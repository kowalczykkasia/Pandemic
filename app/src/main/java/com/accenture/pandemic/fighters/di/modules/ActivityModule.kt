package com.accenture.pandemic.fighters.di.modules

import com.accenture.pandemic.fighters.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun mainActivity():
            MainActivity
}
package com.accenture.pandemic.fighters.di.components

import com.accenture.pandemic.fighters.PandemicFighters
import com.accenture.pandemic.fighters.di.modules.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        FragmentModule::class,
        ActivityModule::class,
        ContextModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        FirebaseModule::class,
        RepositoryModule::class,
        RemoteModule::class,
        DatabaseModule::class,
        MyGeocoderModule::class,
        FacebookModule::class
    ]
)

interface AppComponent : AndroidInjector<PandemicFighters> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<PandemicFighters>()
}
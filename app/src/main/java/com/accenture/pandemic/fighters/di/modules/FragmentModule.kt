package com.accenture.pandemic.fighters.di.modules

import com.accenture.pandemic.fighters.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun bindSplashScreenFragment(): SplashScreenFragment

    @ContributesAndroidInjector
    internal abstract fun bindLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    internal abstract fun bindRegistrationScreenFragment(): RegistrationScreenFragment

    @ContributesAndroidInjector
    internal abstract fun bindMapFragment(): MapFragment

    @ContributesAndroidInjector
    internal abstract fun bindFiltersFragment(): FiltersFragment

    @ContributesAndroidInjector
    internal abstract fun bindReportVirusFragment(): ReportVirusFragment

    @ContributesAndroidInjector
    internal abstract fun bindNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    internal abstract fun bindReportEditVirusLocationFragment(): EditVirusLocationFragment

    @ContributesAndroidInjector
    internal abstract fun bindReportVirusListFragment(): VirusListFragment

    @ContributesAndroidInjector
    internal abstract fun bindDatePickerFragment(): DatePickerFragment
    @ContributesAndroidInjector
    internal abstract fun bindProfileFragment(): ProfileFragment
}
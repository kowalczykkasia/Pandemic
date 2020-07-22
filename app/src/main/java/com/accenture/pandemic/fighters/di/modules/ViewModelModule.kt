package com.accenture.pandemic.fighters.di.modules

import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.viewModels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(key = SplashScreenViewModel::class)
    abstract fun bindSplashScreenViewModel(viewModel: SplashScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = RegistrationScreenViewModel::class)
    abstract fun bindRegistrationScreenViewModel(viewModel: RegistrationScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = MapViewModel::class)
    abstract fun bindMapViewModel(viewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = FiltersViewModel::class)
    abstract fun bindFiltersViewModel(viewModel: FiltersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = ReportVirusViewModel::class)
    abstract fun bindReportVirusViewModel(viewModel: ReportVirusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = NotificationsViewModel::class)
    abstract fun bindNotificationsViewModel(viewModel: NotificationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = EditVirusLocationViewModel::class)
    abstract fun bindEditVirusLocationViewModel(viewModel: EditVirusLocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = VirusListViewModel::class)
    abstract fun bindVirusListViewModel(viewModel: VirusListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = DatePickerViewModel::class)
    abstract fun bindDatePickerViewModel(viewModel: DatePickerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(key = ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

}
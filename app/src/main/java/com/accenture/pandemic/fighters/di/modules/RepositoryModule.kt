package com.accenture.pandemic.fighters.di.modules

import com.accenture.pandemic.fighters.repository.Repository
import com.accenture.pandemic.fighters.repository.remote.VirusLocationService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(
        virusLocationService: VirusLocationService
    ) = Repository(virusLocationService)

}
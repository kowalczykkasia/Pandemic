package com.accenture.pandemic.fighters.repository


import com.accenture.pandemic.fighters.Models.VirusLocationBody
import com.accenture.pandemic.fighters.repository.remote.VirusLocationService

const val PAGE_SIE = 500
class Repository(private val virusLocationService: VirusLocationService) {

    suspend fun getLocationList() = virusLocationService.getAllLocations(PAGE_SIE.toLong())

    suspend fun addNewVirus(virusLocation: VirusLocationBody) = virusLocationService.addNewVirusLocation(virusLocation)
}

package com.accenture.pandemic.fighters.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accenture.pandemic.fighters.Models.*
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.repository.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportVirusViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    fun addReport(virusLocation: VirusLocationBody){
        viewModelScope.launch {
            repository.addNewVirus(virusLocation)
        }
    }

    fun initTestList(context: Context) : List<ReportButtonModel>{
        return mutableListOf(ReportButtonModel(context.getString(R.string.yes),ButtonName.YES),
            ReportButtonModel(context.getString(R.string.pending), ButtonName.NO),
            ReportButtonModel(context.getString(R.string.dontknow), ButtonName.DONTKNOW)
        )
    }

    fun initSymptomsList(context: Context) : List<ReportButtonModel>{
        return mutableListOf(
            ReportButtonModel(context.getString(R.string.fever), ButtonName.FEVER),
            ReportButtonModel(context.getString(R.string.cough), ButtonName.COUGH),
            ReportButtonModel(context.getString(R.string.breathlessness), ButtonName.BREATHLESSNESS)
        )
    }
    fun initSymptomYNsList(context: Context) : List<ReportButtonModel>{
        return mutableListOf(ReportButtonModel(context.getString(R.string.yes),ButtonName.YES),
            ReportButtonModel(context.getString(R.string.no), ButtonName.NO)
        )
    }
}

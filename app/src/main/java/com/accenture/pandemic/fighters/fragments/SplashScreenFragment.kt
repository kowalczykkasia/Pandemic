package com.accenture.pandemic.fighters.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.viewModels.SplashScreenViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.splash_fragment.view.*
import javax.inject.Inject

class SplashScreenFragment :DaggerFragment() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory
    private val viewModel by viewModels<SplashScreenViewModel> { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false).apply {
            stopButton.setOnClickListener {
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashFragment2ToLoginFragment())
            }
        }
    }
}

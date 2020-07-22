package com.accenture.pandemic.fighters.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.accenture.pandemic.fighters.MainActivity
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.viewModels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<LoginViewModel> { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false).apply {
            loginButton.isEnabled = false
            setFocus(emailEditText)
            setFocus(passwordEditText)

            signupButton.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationScreenFragment())
                emailEditText.text = null
                passwordEditText.text = null
            }
            back.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSplashFragment())
            }

            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {}
                })

            loginButton.setOnClickListener {
                blockScreen()
                viewModel.logIn(emailEditText.text.toString(), passwordEditText.text.toString())
            }

            google_button.setOnClickListener {
                blockScreen()
                val signInIntent = viewModel
                    .googleSignIn(this@LoginFragment, activity as Activity)
                    .signInIntent
                startActivityForResult(signInIntent, viewModel.REQUEST)
            }

            with(viewModel) {
                done.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        1 -> {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment())
                        }
                        2 -> {
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                            viewModel.block.value?.dismiss()
                        }
                        3 -> {
                            viewModel.block.value?.dismiss()
                        }
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == viewModel.REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                viewModel.firebaseAuthWithGoogle(account.idToken!!, activity as Activity)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed")
                viewModel.done.postValue(3)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.block.value?.dismiss()
    }

    private fun setFocus(editText: EditText) {
        editText.setOnFocusChangeListener { v, _ ->
            setDefault(emailEditText)
            setDefault(passwordEditText)

            editText.gravity = Gravity.CENTER_VERTICAL
            editText.setPaddingRelative(120, 0, 0, 0)
            v.setBackgroundResource(R.drawable.rounded_button_back)
        }

        editText.addTextChangedListener {
            if(editText.text.isNotEmpty()){
                if(editText == emailEditText) {
                    loginButton.isEnabled = passwordEditText.text.isNotEmpty()
                }else{
                    loginButton.isEnabled = emailEditText.text.isNotEmpty()
                }
            }else
                loginButton.isEnabled = false
        }

    }

    private fun setDefault(editText: EditText) {
        editText.setBackgroundResource(R.drawable.borderless_edittext)
        editText.setPaddingRelative(0, 0, 0, 0)
        editText.gravity = Gravity.CENTER
    }

    private fun blockScreen() {
        viewModel.block.value = (activity as MainActivity).block()
    }
}


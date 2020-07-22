package com.accenture.pandemic.fighters.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.accenture.pandemic.fighters.MainActivity

import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.viewModels.RegistrationScreenViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.registration_screen_fragment.*
import kotlinx.android.synthetic.main.registration_screen_fragment.view.emailEditText
import kotlinx.android.synthetic.main.registration_screen_fragment.view.loginButton
import kotlinx.android.synthetic.main.registration_screen_fragment.view.nameEditText
import kotlinx.android.synthetic.main.registration_screen_fragment.view.passwordEditText
import kotlinx.android.synthetic.main.registration_screen_fragment.view.signupButton
import javax.inject.Inject

class RegistrationScreenFragment : DaggerFragment() {

    @Inject
    lateinit var factory : ViewModelProvider.Factory
    private val viewModel by viewModels<RegistrationScreenViewModel> { factory  }
    private var strongPass = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registration_screen_fragment, container, false).apply {
            setFocus(nameEditText)
            setFocus(emailEditText)
            setFocus(passwordEditText)
            signupButton.isEnabled = false

            passwordEditText.addTextChangedListener { pass ->
                strongPass = validate(passwordEditText.text.toString())
            }

            loginButton.setOnClickListener {
                findNavController().navigate(RegistrationScreenFragmentDirections.actionRegistrationScreenFragmentToLoginFragment())
            }

            signupButton.setOnClickListener {
                blockScreen()
                viewModel.signIn(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()

                )
            }

            with(viewModel){
                done.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        2 -> {
                            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                            viewModel.block.value?.dismiss()
                        }
                        else -> {
                            Toast.makeText(context, "New user created", Toast.LENGTH_SHORT).show()
                            viewModel.updateUserName(nameEditText.text.toString())
                            findNavController().navigate(RegistrationScreenFragmentDirections.actionRegistrationScreenFragmentToMapFragment())
                        }
                    }
                })
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.block.value?.dismiss()
    }

    private fun validate(pass: String) : Boolean{
        if(pass.length in 8..10){
            if(pass.matches(Regex(".*[0-9]+.*")) && pass.matches(Regex(".*[A-Z]+.*")) && pass.matches(Regex(".*[a-z]+.*"))) {
                return true
            }
        }
        return false
    }

    private fun setFocus(editText: EditText){
        editText.setOnFocusChangeListener { v, _ ->

            setDefault(nameEditText)
            setDefault(emailEditText)
            setDefault(passwordEditText)

            editText.gravity = Gravity.CENTER_VERTICAL
            editText.setPaddingRelative(120, 0, 0, 0)
            v.setBackgroundResource(R.drawable.rounded_button_back)
            validation(editText)
        }

        editText.addTextChangedListener {
            if(editText.text.isNotEmpty()){
                validation(editText)
            }else{
                signupButton.isEnabled = false
                setFaulty(editText)
            }
        }
    }

    private fun validation(editText: EditText){
        when (editText) {
            emailEditText -> {
                val str = editText.text.toString()
                if(Patterns.EMAIL_ADDRESS.matcher(str).matches())
                    setCorrect(editText)
                else
                    setFaulty(editText)
                signupButton.isEnabled = (passwordEditText.text.isNotEmpty() && nameEditText.text.isNotEmpty() && strongPass)
            }
            nameEditText -> {
                if(editText.text.toString().contains(Regex(".+")))
                    setCorrect(editText)
                else
                    setFaulty(editText)
                signupButton.isEnabled = (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() && strongPass)
            }
            else -> {
                strongPass =  validate(editText.text.toString())
                if(strongPass)
                    setCorrect(editText)
                else
                    setFaulty(editText)
                signupButton.isEnabled = (emailEditText.text.isNotEmpty() && nameEditText.text.isNotEmpty() && strongPass)
            }
        }
    }

    private fun setFaulty(editText: EditText){
        editText.setBackgroundResource(R.drawable.error_rounded_button)
        editText.setPaddingRelative(0,0,0,0)
        editText.gravity= Gravity.CENTER
        when (editText) {
            emailEditText -> {
                validEmail.visibility = View.VISIBLE
            }
            nameEditText -> {
                validName.visibility = View.VISIBLE
            }
            else -> {
                validPass.visibility = View.VISIBLE
            }
        }
    }

    private fun setCorrect(editText: EditText){
        editText.setBackgroundResource(R.drawable.rounded_button_back)
        editText.setPaddingRelative(0,0,0,0)
        editText.gravity= Gravity.CENTER
        when (editText) {
            emailEditText -> {
                validEmail.visibility = View.GONE
            }
            nameEditText -> {
                validName.visibility = View.GONE
            }
            else -> {
                validPass.visibility = View.GONE
            }
        }
    }

    private fun setDefault(editText: EditText){
        editText.setBackgroundResource(R.drawable.borderless_edittext)
        editText.setPaddingRelative(0,0,0,0)
        editText.gravity= Gravity.CENTER
    }

    private fun blockScreen(){
        viewModel.block.value = (activity as MainActivity).block()
    }
}

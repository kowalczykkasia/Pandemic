package com.accenture.pandemic.fighters.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
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
import com.accenture.pandemic.fighters.Models.GALLERY_REQUEST_CODE
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.toEditable
import com.accenture.pandemic.fighters.viewModels.ProfileViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.view.*
import javax.inject.Inject


class ProfileFragment : DaggerFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<ProfileViewModel> { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false).apply {
            etName.text = viewModel.getUserName().toEditable()
            etEmail.text = viewModel.getUserEmail().toEditable()
            addEditTextListener(etName)
            addEditTextListener(etEmail)


            with(viewModel) {
                photoBmp.observe(viewLifecycleOwner, Observer {
                        setPhoto(ivProfileImage, it, context)
                })
                updateDone.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        Toast.makeText(context, getString(R.string.not_updated), Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMapFragment())
                    } else {
                        Toast.makeText(context, getString(R.string.updated), Toast.LENGTH_SHORT)
                            .show()
                        viewModel.block.value?.dismiss()
                    }
                })
            }
            btnChangePhoto.setOnClickListener {
                pickFromGallery()
            }

            btnDeletePhoto.setOnClickListener {
                ivProfileImage.setImageResource(R.drawable.no_profile_picture)
                viewModel.photoBmp.postValue((ivProfileImage.drawable as BitmapDrawable).bitmap)
            }
            ivBack.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMapFragment())
            }
            btnSaveChanges.setOnClickListener {
                blockScreen()
                viewModel.updateData(
                    ivProfileImage,
                    etEmail.text.toString(),
                    etName.text.toString()
                )

            }
            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMapFragment())
                    }
                })
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val mimeTypes =
            arrayOf("image/jpeg", "image/png", "image/jpg")

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun validation(editText: EditText) {
        when (editText) {
            etEmail -> {
                val str = editText.text.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(str).matches())
                    setCorrect(editText)
                else
                    setFaulty(editText)
                btnSaveChanges.isEnabled = etName.text.isNotEmpty()
            }
            etName -> {
                if (editText.text.toString().contains(Regex(".+")))
                    setCorrect(editText)
                else
                    setFaulty(editText)
                btnSaveChanges.isEnabled = etEmail.text.isNotEmpty()
            }
        }
    }

    private fun setFaulty(editText: EditText) {
        when (editText) {
            etEmail -> tvValidEmail.visibility = View.VISIBLE
            etName -> tvValidName.visibility = View.VISIBLE
        }
    }

    private fun setCorrect(editText: EditText) {
        when (editText) {
            etEmail -> tvValidEmail.visibility = View.GONE
            etName -> tvValidName.visibility = View.GONE
        }
    }

    private fun addEditTextListener(editText: EditText) {
        editText.addTextChangedListener {
            if (editText.text.isNotEmpty()) {
                validation(editText)
            } else {
                btnSaveChanges.isEnabled = false
                setFaulty(editText)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                val imageUri: Uri? = data?.data
                val bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                viewModel.photoBmp.postValue(bitmap)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.block.value?.dismiss()
    }

    private fun blockScreen() {
        viewModel.block.value = (activity as MainActivity).block()
    }
}

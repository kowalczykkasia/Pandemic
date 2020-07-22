package com.accenture.pandemic.fighters.fragments

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.OnBackPressedCallback
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.accenture.pandemic.fighters.MainActivity
import com.accenture.pandemic.fighters.Models.ButtonName
import com.accenture.pandemic.fighters.Models.ButtonType
import com.accenture.pandemic.fighters.Models.DEFAULT_DISTANCE
import com.accenture.pandemic.fighters.Models.Notifications
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.adapters.ReportButtonsAdapter
import com.accenture.pandemic.fighters.viewModels.NotificationsViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.notifications_fragment.*
import kotlinx.android.synthetic.main.notifications_fragment.view.*
import kotlinx.android.synthetic.main.notifications_fragment.view.RvWithin
import kotlinx.android.synthetic.main.report_button.view.*
import javax.inject.Inject

class NotificationsFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<NotificationsViewModel> { factory }
    private val args: NotificationsFragmentArgs by navArgs()


    private val notificationButtons by lazy {
        ReportButtonsAdapter{button,view ->
            RvNotify.forEach { v -> setButtonShape(v.btnSymptom, ButtonType.BASIC)}
            setButtonShape(view.btnSymptom, ButtonType.SYMPTOMS)

            viewModel.updateNotificationsType(button.type)
            viewModel.updateNotificationTopic(activity as MainActivity)
        }
    }

    private val distanceButtons by lazy {
        ReportButtonsAdapter{button,view ->
            RvWithin.forEach { v -> setButtonShape(v.btnSymptom, ButtonType.BASIC)}
            setButtonShape(view.btnSymptom, ButtonType.SYMPTOMS)

            if(button.type == ButtonName.DISTANCE){
                showSeekBar()
            }else{
                hideSeekBar()
            }
            viewModel.updateNotificationsDistance(button.type)
            viewModel.updateNotificationTopic(activity as MainActivity)

            when(button.type){
                ButtonName.MYCITY ->{
                    args.currentLocation?.let {
                        view.btnSymptom.text =  Geocoder(context).getFromLocation(it.latitude, it.longitude, 1)[0].locality
                    }
                }
                ButtonName.MYCOUNTRY ->{
                    args.currentLocation?.let {
                        view.btnSymptom.text =  Geocoder(context).getFromLocation(it.latitude, it.longitude, 1)[0].countryName
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notifications_fragment, container, false).apply {

            notificationButtons.submitList(viewModel.initNotifyList(context))
            RvNotify.adapter = notificationButtons

            if ((activity as MainActivity).checkPermission()) {
                distanceButtons.submitList(viewModel.initAddedAroundList(context))
                RvWithin.adapter = distanceButtons
            }else
                textAround.text = ""

            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    distance.text = "${progress*50}"
                    viewModel.updateDistance((distance.text.toString().toInt()))
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            back.setOnClickListener {
                findNavController().navigate(NotificationsFragmentDirections.actionNotificationsFragmentToMapFragment())
            }

            on_off.setOnCheckedChangeListener { _, isChecked ->
                if(!isChecked) {
                    reset.performClick()
                }
                viewModel.updateNotifications()
                viewModel.updateNotificationTopic(activity as MainActivity)
                showView()
            }

            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(NotificationsFragmentDirections.actionNotificationsFragmentToMapFragment())
                    }
                })

            reset.setOnClickListener {
                RvWithin.forEach { v -> setButtonShape(v.btnSymptom, ButtonType.BASIC)}
                RvNotify.forEach { v ->
                    if(v.btnSymptom.text == context.getString(R.string.All))
                        setButtonShape(v.btnSymptom, ButtonType.SYMPTOMS)
                    else
                        setButtonShape(v.btnSymptom, ButtonType.BASIC)}
                hideSeekBar()
                defaultSharedPrefs()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(viewModel.getNotificationState() == Notifications.ON){
            viewModel.updateNotifications()
            on_off.performClick()
        }
        if(viewModel.getNotificationDistance() == ButtonName.DISTANCE)
            showSeekBar()
    }

    override fun onStop() {
        super.onStop()
        if(viewModel.getNotificationState() == Notifications.OFF){
            defaultSharedPrefs()
        }
    }

    private fun defaultSharedPrefs(){
        viewModel.updateNotificationsType(ButtonName.ALL)
        viewModel.updateNotificationsDistance(ButtonName.NONE)
        viewModel.updateDistance(DEFAULT_DISTANCE)
    }

    private fun showSeekBar(){
        seekBar.progress = viewModel.getDistance()/50
        seekBar.visibility = View.VISIBLE
        textRadius.visibility = View.VISIBLE
        distance.visibility = View.VISIBLE
        km.visibility = View.VISIBLE
    }

    private fun hideSeekBar(){
        seekBar.progress = 3
        seekBar.visibility = View.GONE
        textRadius.visibility = View.GONE
        distance.visibility = View.GONE
        km.visibility = View.GONE
        viewModel.updateDistance(DEFAULT_DISTANCE)
    }

    private fun showView(){
        if(viewModel.getNotificationState() == Notifications.ON) {
            RvWithin.visibility = View.VISIBLE
            RvNotify.visibility = View.VISIBLE
            textAround.visibility = View.VISIBLE
            textNotify.visibility = View.VISIBLE
            reset.visibility = View.VISIBLE
        }else{
            RvWithin.visibility = View.GONE
            RvNotify.visibility = View.GONE
            textAround.visibility = View.GONE
            textNotify.visibility = View.GONE
            reset.visibility = View.GONE

            seekBar.visibility = View.GONE
            textRadius.visibility = View.GONE
            distance.visibility = View.GONE
            km.visibility = View.GONE
        }
    }


    private fun setButtonShape(button: Button, name: ButtonType){
        if(name == ButtonType.BASIC){
            button.setBackgroundResource(R.drawable.basic_button_shape)
            button.setTextColor(resources.getColor(R.color.darkGrey))
        }else{
            button.setBackgroundResource(R.drawable.notifications_button_shape)
            button.setTextColor(Color.WHITE)
        }
    }



}


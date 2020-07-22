package com.accenture.pandemic.fighters.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.accenture.pandemic.fighters.Models.*
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.adapters.ReportButtonsAdapter
import com.accenture.pandemic.fighters.utils.getShortAddress
import com.accenture.pandemic.fighters.viewModels.ReportVirusViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.report_button.view.*
import kotlinx.android.synthetic.main.report_virus_fragment.*
import kotlinx.android.synthetic.main.report_virus_fragment.view.*
import kotlinx.android.synthetic.main.report_virus_fragment.view.RwTested
import javax.inject.Inject

class ReportVirusFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<ReportVirusViewModel> { factory }
    private val args: ReportVirusFragmentArgs by navArgs()

    private val reportTestAdapter by lazy {
        ReportButtonsAdapter{ _,view ->
            RwTested.forEach { view ->
                setButtonShape(view.btnSymptom, ButtonType.BASIC)
            }
            setButtonShape(view.btnSymptom, ButtonType.TESTED)
        }
    }

    private val reportSymptomsYNAdapter by lazy {
        ReportButtonsAdapter{virus,view ->
            RwSymptoms_YN.forEach { view ->
                setButtonShape(view.btnSymptom, ButtonType.BASIC)
            }
            if(virus.type == ButtonName.NO) {
                setDefaultButtons()
            } else {
                Fever.isEnabled = true
                Cough.isEnabled = true
                Breath.isEnabled = true
                Fever.visibility = View.VISIBLE
                Cough.visibility = View.VISIBLE
                Breath.visibility = View.VISIBLE
            }
            setButtonShape(view.btnSymptom, ButtonType.SYMPTOMS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_virus_fragment, container, false).apply {
            args.currentLocation?.let{ it ->
                location.text = Geocoder(context).getFromLocation(it.latitude, it.longitude, 1)
                    .firstOrNull()?.getShortAddress(context)
                    ?: getString(R.string.unknownLocation)
            }

            setButtonListener(Fever)
            setButtonListener(Cough)
            setButtonListener(Breath)

            back.setOnClickListener {
                findNavController().navigate(ReportVirusFragmentDirections.actionReportVirusFragmentToMapFragment())
            }

            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(ReportVirusFragmentDirections.actionReportVirusFragmentToMapFragment())
                    }
                })

            reportTestAdapter.submitList(viewModel.initTestList(context))
            RwTested.adapter = reportTestAdapter

            reportSymptomsYNAdapter.submitList(viewModel.initSymptomYNsList(context))
            RwSymptoms_YN.adapter = reportSymptomsYNAdapter

            btnEdit.setOnClickListener {
                findNavController().navigate(ReportVirusFragmentDirections.actionReportVirusFragmentToEditVirusLocationFragment(args.currentLocation,args.nearbyLocations))
            }

            btnSubmit.setOnClickListener {
               showPopupToConfirm()
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setDefaultButtons()
    }

    private fun showPopupConfirming(){
        val view = layoutInflater.inflate(R.layout.popup_add_new_report, null)
        val popup = PopupWindow(view, coordinator.width , coordinator.height)
        val buttonLeft = view.findViewById<Button>(R.id.left)
        val buttonRight = view.findViewById<Button>(R.id.right)
        popup.contentView = view

        buttonLeft.setOnClickListener {
            popup.dismiss()
            RwTested.forEach { view ->
                if(view.btnSymptom.text.toString() == getString(R.string.yes))
                    setButtonShape(view.btnSymptom, ButtonType.TESTED)
                else
                    setButtonShape(view.btnSymptom, ButtonType.BASIC)
            }
            setDefaultButtons()

            RwSymptoms_YN.forEach { view ->
                if(view.btnSymptom.text.toString() == getString(R.string.no))
                    setButtonShape(view.btnSymptom, ButtonType.SYMPTOMS)
                else
                    setButtonShape(view.btnSymptom, ButtonType.BASIC)
            }
            descriptionSymptoms.text = null
        }
        buttonRight.setOnClickListener {
            findNavController().navigate(ReportVirusFragmentDirections.actionReportVirusFragmentToMapFragment())
            popup.dismiss()
        }

        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    private fun showPopupToConfirm(){
        val view = layoutInflater.inflate(R.layout.popup_add_new_report, null)
        val popup = PopupWindow(view, coordinator.width , coordinator.height)
        val buttonLeft = view.findViewById<Button>(R.id.left)
        val buttonRight = view.findViewById<Button>(R.id.right)
        val textView = view.findViewById<TextView>(R.id.notification)
        view.findViewById<TextView>(R.id.link).visibility = View.VISIBLE
        popup.contentView = view

        buttonLeft.text = getString(R.string.Cancel)
        buttonRight.text = getString(R.string.submit)
        textView.text = getString(R.string.info_text)

        textView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pandemicfighters/home")))
        }

        buttonLeft.setOnClickListener {
            popup.dismiss()
        }
        buttonRight.setOnClickListener {
            val symptoms = mutableListOf<Description>()
            var tested = ""
            RwTested.forEach { v -> if(v.btnSymptom.currentTextColor == context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorWhite)}) tested = v.btnSymptom.text.toString() }
            when(tested){
                getString(R.string.yes) -> {
                    tested = "Tested"
                }
                getString(R.string.pending) -> {
                    tested = "Pending"
                }
                else -> {
                    tested = "I don't know"
                }
            }

            if(Fever.text.isNotEmpty()) symptoms.add(Description("Fever"))
            if(Cough.text.isNotEmpty()) symptoms.add(Description("Cough"))
            if(Breath.text.isNotEmpty()) symptoms.add(Description("Shortness of Breath"))

            args.currentLocation?.let {
                viewModel.addReport(
                    VirusLocationBody(
                        FieldsToWrite(Description(descriptionSymptoms.text.toString()),
                            Latitude(it.latitude), Tested(tested),
                            SymptomsList(Values(symptoms)), Longitude(it.longitude), TimeStamp((System.currentTimeMillis()/1000).toString()),
                            SelfReported(true))
                    )
                )
            }
            popup.dismiss()
            showPopupConfirming()
        }
        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    private fun setButtonShape(button: Button, name: ButtonType){
        when(name){
            ButtonType.TESTED ->{
                button.setBackgroundResource(R.drawable.report_virus_button_shape)
                button.setTextColor(Color.WHITE)
            }
            ButtonType.SYMPTOMS -> {
                button.setBackgroundResource(R.drawable.notifications_button_shape)
                button.setTextColor(Color.WHITE)
            }
            ButtonType.BASIC ->{
                button.setBackgroundResource(R.drawable.basic_button_shape)
                button.setTextColor(resources.getColor(R.color.darkGrey))
            }
        }
    }

    private fun setButtonListener(button: Button){
        button.setOnClickListener {
            if(button.currentTextColor ==  Color.WHITE){
                setButtonShape(button,ButtonType.BASIC)
            }else
                setButtonShape(button,ButtonType.SYMPTOMS)
        }
    }

    private fun setDefaultButtons(){
        setButtonShape(Fever, ButtonType.BASIC)
        setButtonShape(Cough, ButtonType.BASIC)
        setButtonShape(Breath, ButtonType.BASIC)
        Fever.isEnabled = false
        Cough.isEnabled = false
        Breath.isEnabled = false
        Fever.visibility = View.GONE
        Cough.visibility = View.GONE
        Breath.visibility = View.GONE
    }
}

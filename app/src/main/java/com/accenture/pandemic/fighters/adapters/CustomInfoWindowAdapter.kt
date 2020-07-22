package com.accenture.pandemic.fighters.adapters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.accenture.pandemic.fighters.Models.Fields
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.MyGeocoder
import com.accenture.pandemic.fighters.utils.setCoronaTestText
import com.accenture.pandemic.fighters.utils.setSymptomsText
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import javax.inject.Inject

class CustomInfoWindowAdapter @Inject constructor(
    private val context: Context,
    private val myGeocoder: MyGeocoder
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View? = null

    override fun getInfoWindow(marker: Marker?):View? {
        val model = marker?.tag as Fields?
        model?.let {

            val view = View.inflate(context, R.layout.custom_map_info_window, null)
            val location = view.findViewById<TextView>(R.id.tvRoad)
            val symptoms = view.findViewById<TextView>(R.id.tvSymptoms)
            val tested = view.findViewById<TextView>(R.id.tvTested)
            val description = view.findViewById<TextView>(R.id.tvDescription)
            val checked = view.findViewById<ImageView>(R.id.ivChecked)

            location.text = myGeocoder.getRoadWithNumberFromLocation(
                context,
                LatLng(model.latitude.doubleValue, model.longitude.doubleValue)
            )
            if (model.description.stringValue != "")
                description.text = model.description.stringValue
            else description.visibility = View.GONE


            symptoms.text = setSymptomsText(model.symptoms.arrayValue, context)
            tested.text = setCoronaTestText(model.tested.stringValue, context)
            if (model.tested.stringValue  == context.getString(R.string.tested))
                checked.visibility = View.VISIBLE

            return view
        } ?: return null
    }
}
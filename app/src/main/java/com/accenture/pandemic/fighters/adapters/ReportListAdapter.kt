package com.accenture.pandemic.fighters.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.accenture.pandemic.fighters.Models.Fields
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.MyGeocoder
import com.accenture.pandemic.fighters.utils.getDayMonthYearHourMinutes

import com.accenture.pandemic.fighters.utils.setCoronaTestText
import com.accenture.pandemic.fighters.utils.setSymptomsText
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.report_list_item.view.*


class ReportListAdapter(private val myGeocoder: MyGeocoder) :
    ListAdapter<Fields, ReportListAdapter.ReportListAdapterViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListAdapterViewHolder =
        ReportListAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.report_list_item, parent, false)
        )

    override fun onBindViewHolder(holder: ReportListAdapterViewHolder, position: Int) {
        holder.bind(getItem(position),myGeocoder)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Fields>() {

            override fun areItemsTheSame(
                oldItem: Fields, newItem: Fields
            ): Boolean =
                oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude

            override fun areContentsTheSame(
                oldItem: Fields, newItem: Fields
            ): Boolean =
                oldItem == newItem
        }
    }

    class ReportListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(fields: Fields, myGeocoder: MyGeocoder) {
            itemView.apply {
                tvLocation.text = myGeocoder.getTextAddressFromLocation(
                    context,
                    LatLng(fields.latitude.doubleValue, fields.longitude.doubleValue)
                )

                if (fields.description.stringValue != "")
                    tvDescription.text = fields.description.stringValue
                else tvDescription.visibility = View.GONE

                tvDateTime.text = getDayMonthYearHourMinutes(fields.timeStamp.stringValue.toInt())
                tvSymptoms.text = setSymptomsText(fields.symptoms.arrayValue, context)
                tvTested.text = setCoronaTestText(fields.tested.stringValue, context)

                tvMore.setOnClickListener { handleVisibility(ltMore,tvMore) }
                ltMore.setOnClickListener { handleVisibility(ltMore,tvMore) }
            }
        }

        private fun handleVisibility(viewGroup: ViewGroup, tvMore: TextView) {
            if (viewGroup.visibility == View.GONE) {
                tvMore.visibility = View.GONE
                viewGroup.visibility = View.VISIBLE
            } else {
                tvMore.visibility = View.VISIBLE
                viewGroup.visibility = View.GONE
            }
        }

    }
}



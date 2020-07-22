package com.accenture.pandemic.fighters.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.accenture.pandemic.fighters.Models.ButtonName
import com.accenture.pandemic.fighters.Models.ReportButtonModel
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.repository.local.getNotificationsDistance
import com.accenture.pandemic.fighters.repository.local.getNotificationsType
import kotlinx.android.synthetic.main.report_button.view.*

class ReportButtonsAdapter(private val listener: (ReportButtonModel, View) -> Unit)
    : ListAdapter<ReportButtonModel, ReportButtonsAdapter.ReportButtonsViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportButtonsViewHolder =
        ReportButtonsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.report_button, parent, false))


    override fun onBindViewHolder(holder: ReportButtonsViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    public override fun getItem(position: Int): ReportButtonModel {
        return super.getItem(position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReportButtonModel>(){

            override fun areItemsTheSame(oldItem: ReportButtonModel, newItem: ReportButtonModel
            ): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: ReportButtonModel, newItem: ReportButtonModel
            ): Boolean =
                oldItem == newItem
        }
    }

    class ReportButtonsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(report: ReportButtonModel, listener: (ReportButtonModel,View) -> Unit) {

            itemView.apply {
                val sharedPreferences = context.getSharedPreferences("pandemic_app", Context.MODE_PRIVATE)
                btnSymptom.text = report.name
                if(btnSymptom.text.length >= 15)
                    btnSymptom.setTextSize(10F)

                if(report.type == ButtonName.ALL && sharedPreferences.getNotificationsType() == ButtonName.ALL ||
                    report.type == ButtonName.CONFIRMED && sharedPreferences.getNotificationsType() == ButtonName.CONFIRMED ||
                    report.type == ButtonName.SELFREPORTED && sharedPreferences.getNotificationsType() == ButtonName.SELFREPORTED ||
                    report.type == ButtonName.DISTANCE && sharedPreferences.getNotificationsDistance() == ButtonName.DISTANCE ||
                    report.type == ButtonName.MYCITY && sharedPreferences.getNotificationsDistance() == ButtonName.MYCITY ||
                    report.type == ButtonName.MYCOUNTRY && sharedPreferences.getNotificationsDistance() == ButtonName.MYCOUNTRY){

                    btnSymptom.setBackgroundResource(R.drawable.notifications_button_shape)
                    btnSymptom.setTextColor(Color.WHITE)
                }

                btnSymptom.setOnClickListener { listener(report,itemView) }

                if(report.type == ButtonName.YES)
                    btnSymptom.performClick()
            }
        }

    }


}
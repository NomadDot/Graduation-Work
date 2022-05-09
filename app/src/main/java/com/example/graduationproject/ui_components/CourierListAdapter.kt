package com.example.graduationproject.ui_components

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.model.Courier

class CourierListAdapter(
    private var listOfCouriers: ArrayList<Courier>,
    private val callback: OnCourierItemClick
) : RecyclerView.Adapter<CourierListAdapter.CourierViewHolder>() {

    inner class CourierViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourierName = itemView.findViewById<TextView>(R.id.tvCourierName)
        val tvCourierAge = itemView.findViewById<TextView>(R.id.tvCourierAge)
        val tvRate = itemView.findViewById<TextView>(R.id.tvCourierRateValue)
        val ivAvatar = itemView.findViewById<ImageView>(R.id.ivCourierImage)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourierListAdapter.CourierViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.courier_item,
                parent,
                false
            )

        return CourierViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourierListAdapter.CourierViewHolder, position: Int) {
       val currentCourier = listOfCouriers[position]
        holder.tvCourierName.text = " ${currentCourier.name} ${currentCourier.lastName}"
        holder.tvCourierAge.text = currentCourier.age
        holder.tvRate.text = currentCourier.rate
    }

    override fun getItemCount(): Int {
        return listOfCouriers.size
    }
}
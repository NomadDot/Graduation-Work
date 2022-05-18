package com.example.graduationproject.ui.couriers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.graduationproject.R
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import kotlinx.coroutines.NonDisposableHandle.parent

class CourierListAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Courier>,
) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return  dataSource.size
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val courierItemView = inflater.inflate(R.layout.courier_item, parent, false)

        val tvCourierName = courierItemView.findViewById<TextView>(R.id.tvCourierName)
        val tvCourierAge = courierItemView.findViewById<TextView>(R.id.tvCourierAge)
        val tvCourierStatus = courierItemView.findViewById<TextView>(R.id.tvCourierStatusValue)
        val tvRate = courierItemView.findViewById<TextView>(R.id.tvCourierRateValue)
        val tvPhoneNumber = courierItemView.findViewById<TextView>(R.id.tvPhoneNumber)

        tvCourierName.text = " ${dataSource[position].name} ${dataSource[position].lastName}"
        tvCourierAge.text = dataSource[position].age
        tvRate.text = dataSource[position].rate
        tvCourierStatus.text = ""
        tvPhoneNumber.text = "${tvPhoneNumber.text}${dataSource[position].phoneNumber}"

        return courierItemView
    }
}
package com.example.graduationproject.ui.orders.orderList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.graduationproject.R
import com.example.graduationproject.model.Order

class OrderListAdapter(
    private val context: Context,
    private val dataSource: ArrayList<Order>,
    private val onButtonCallback: ButtonChooseCallback
) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
       return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val orderItemView = inflater.inflate(R.layout.item_order, parent, false)
        val tvOrder = orderItemView.findViewById<TextView>(R.id.tvOrderNumber)
        val tvPlace = orderItemView.findViewById<TextView>(R.id.tvPlace)
        val tvOrigin = orderItemView.findViewById<TextView>(R.id.tvOrigin)
        val tvDestination = orderItemView.findViewById<TextView>(R.id.tvDestination)
        val tvProducts = orderItemView.findViewById<TextView>(R.id.tvProducts)
        val btnChooseOrder = orderItemView.findViewById<Button>(R.id.btnChooseOrder)

        tvOrder.text = "# ${dataSource[position].orderNumber}"
        tvPlace.text = "${tvPlace.text}${dataSource[position].shopName}"
        tvProducts.text = "${tvProducts.text}${dataSource[position].product1}|${dataSource[position].product2}|${dataSource[position].product3}"
        tvOrigin.text = "${tvOrigin.text}${dataSource[position].placeFrom}"
        tvDestination.text = "${tvDestination.text}${dataSource[position].placeTo}"


        if (dataSource[position].status == "processing") {
            btnChooseOrder.isEnabled = false
            btnChooseOrder.backgroundTintList = context.resources.getColorStateList(R.color.main_theme)
            btnChooseOrder.text = "Виконується"
        } else {
            btnChooseOrder.setOnClickListener {
                onButtonCallback.configureMapForItem(dataSource[position])
            }
        }


        return orderItemView
    }
}
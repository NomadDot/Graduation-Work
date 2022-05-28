package com.example.graduationproject.ui.flows.admin_flow

import android.annotation.SuppressLint
import com.example.graduationproject.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.graduationproject.model.Order


class CustomExpandableListAdapter internal constructor(
    private val context: Context,
    private val ordersNumberList: ArrayList<String>,
    private val orderList: HashMap<String, Order>
) : BaseExpandableListAdapter() {

    companion object {
        const val DURATION_MILLIS: Long = 250
        const val BASE_ANGLE: Float = 0F
        const val ROTATE_ANGLE: Float = 90F
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.orderList[this.ordersNumberList[listPosition]] as Order
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var childConvertView = convertView

        val expandedOrder = getChild(listPosition, expandedListPosition) as Order

        if (childConvertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            childConvertView = layoutInflater.inflate(R.layout.order_expanded_item, null)
        }
        childConvertView!!.isEnabled = false

        val tvFrom = childConvertView.findViewById<TextView>(R.id.tvFromValue)
        val tvTo = childConvertView.findViewById<TextView>(R.id.tvToValue)
        val tvDistance = childConvertView.findViewById<TextView>(R.id.distanceValue)
        val tvCourier = childConvertView.findViewById<TextView>(R.id.courier)
        val tvProducts = childConvertView.findViewById<TextView>(R.id.tvProducts)
        val container = childConvertView.findViewById<ConstraintLayout>(R.id.container)

        var slideDownAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.slide_down
        )

        tvFrom.text = expandedOrder.placeFrom
        tvTo.text = expandedOrder.placeTo
        tvDistance.text = expandedOrder.distance + "м."
        tvCourier.text = "Кур'єр: ${expandedOrder.courier}"
        tvProducts.text = "Доставлений товар: ${expandedOrder.product1} ,${expandedOrder.product2},${expandedOrder.product3}"

        container.startAnimation(slideDownAnimation)

        return childConvertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }

    override fun getGroup(listPosition: Int): Any {
        return this.ordersNumberList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.ordersNumberList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var groupConvertView = convertView

        val listTitle = getGroup(listPosition) as String

        if (groupConvertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            groupConvertView = layoutInflater.inflate(R.layout.order_expand_item, null)
        }
        val ivIndicator = groupConvertView!!.findViewById<ImageView>(R.id.ivIndicator)

        if (isExpanded) {
            ivIndicator.animate()
                .rotation(ROTATE_ANGLE)
                .setInterpolator(LinearInterpolator())
                .duration = DURATION_MILLIS
        } else {
            ivIndicator
                .animate()
                .rotation(BASE_ANGLE)
                .setInterpolator(LinearInterpolator())
                .duration = DURATION_MILLIS
        }

        val listTitleTextView = groupConvertView.findViewById<TextView>(R.id.tvQuestion)
        listTitleTextView.text = listTitle

        return groupConvertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
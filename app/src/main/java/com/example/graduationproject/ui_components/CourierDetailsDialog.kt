package com.example.graduationproject.ui_components

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.graduationproject.R
import com.example.graduationproject.model.Courier

@SuppressLint("ViewConstructor")
class CourierDetailsDialog(context: Context, builder: Builder) : View(context) {
    private var myView: View = builder.view

    class Builder(context: Context) {
        private val builderContext: Context = context
        var view: View = inflate(builderContext, R.layout.courier_data_sheet_dialog, null)

        private var tvOrderNumber: TextView
        private var tvFrom: TextView
        private var tvTo: TextView
        private var tvPlace: TextView
        private var tvProducts: TextView
        private var tvDistance: TextView
        private var tvPassedDistance: TextView

        private var tvCourierName: TextView
        private var tvCourierAge: TextView
        private var tvCourierRate: TextView
        private var tvCourierPhoneNumber: TextView

        init {
            tvOrderNumber = view.findViewById(R.id.orderNumber)
            tvFrom = view.findViewById(R.id.tvFromValue)
            tvTo = view.findViewById(R.id.tvToValue)
            tvPlace = view.findViewById(R.id.tvPlaceValue)
            tvProducts = view.findViewById(R.id.tvProductsValue)
            tvDistance = view.findViewById(R.id.tvDistanceValue)
            tvCourierName = view.findViewById(R.id.tvCourierName)
            tvCourierAge = view.findViewById(R.id.tvCourierAge)
            tvCourierRate = view.findViewById(R.id.tvCourierRateValue)
            tvPassedDistance = view.findViewById(R.id.tvDistancePassedValue)
            tvCourierPhoneNumber = view.findViewById(R.id.tvPhoneNumber)
        }

        fun setCourierItemView(courier: Courier): Builder {
            tvCourierName.text = "${courier.name} ${courier.lastName}"
            tvCourierAge.text = courier.age
            tvCourierRate.text = courier.rate
            tvCourierPhoneNumber.text = courier.phoneNumber
            return this
        }

        @SuppressLint("SetTextI18n")
        fun setOrderNumber(value: String): Builder {
            tvOrderNumber.text = "Номер: $value"
            return this
        }

        fun setFrom(value: String): Builder {
            tvFrom.text = value
            return this
        }

        fun setTo(value: String): Builder {
            tvTo.text = value
            return this
        }

        fun setPlace(value: String): Builder {
            tvPlace.text = value
            return this
        }

        fun setProducts(value: String): Builder {
            tvProducts.text = value
            return this
        }

        fun setDistance(value: String): Builder {
            tvDistance.text = "$value м."
            return this
        }

        fun setPassedDistance(value: String): Builder {
            tvPassedDistance.text = "$value м."
            return this
        }

        fun build(): CourierDetailsDialog {
            return CourierDetailsDialog(builderContext, this)
        }
    }

    fun getPopUpDialogView(): View {
        return myView
    }
}
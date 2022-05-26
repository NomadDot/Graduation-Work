package com.example.graduationproject.ui.details

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.graduationproject.R

@SuppressLint("ViewConstructor")
class PopUpDialog(context: Context, builder: Builder) : View(context) {

    private var myView: View = builder.view

    class Builder(context: Context)  {
        private val builderContext: Context = context
        var view: View = inflate(builderContext, R.layout.details_sheet_dialog, null)

        private var tvOrderNumber: TextView
        private var tvFrom: TextView
        private var tvTo: TextView
        private var tvPlace: TextView
        private var tvProducts: TextView
        private var tvDistance: TextView
        private var tvPassedDistance: TextView
        private var btnDone: Button
        private var tvTime: TextView

        init {
            tvOrderNumber = view.findViewById(R.id.orderNumber)
            tvFrom = view.findViewById(R.id.tvFromValue)
            tvTo = view.findViewById(R.id.tvToValue)
            tvPlace = view.findViewById(R.id.tvPlaceValue)
            tvProducts = view.findViewById(R.id.tvProductsValue)
            tvDistance = view.findViewById(R.id.tvDistanceValue)
            tvPassedDistance = view.findViewById(R.id.tvDistancePassedValue)
            btnDone = view.findViewById(R.id.btnDone)
            tvTime = view.findViewById(R.id.tvTimeValue)
        }

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
            tvDistance.text = value
            return this
        }

        fun setPassedDistance(value: String): Builder {
            tvPassedDistance.text = value
            return this
        }

        fun setTime(value: String): Builder {
            tvTime.text = value
            return this
        }

        fun onButtonClick(listener: View.OnClickListener): Builder {
            btnDone.setOnClickListener(listener)
            return this
        }

        fun build(): PopUpDialog {
            return PopUpDialog(builderContext, this)
        }
    }

    fun getPopUpDialogView(): View {
        return myView
    }
}
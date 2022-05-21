package com.example.graduationproject.ui.orders.orderList

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.core.Constants.Companion.CURRENT_COURIER
import com.example.graduationproject.core.Constants.Companion.CURRENT_ORDER
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order

class OrderListFragment : Fragment() {

    companion object {
        fun newInstance() = OrderListFragment()
    }

    private lateinit var rvListOfOrders: ListView
    private lateinit var dialog: ProgressDialog
    private lateinit var arrayOfOrders: ArrayList<Order>
    private lateinit var currentCourier: Courier

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_list, container, false)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentCourier = SharedResources.executor.getCourier()!!

        if(currentCourier.order != Constants.EMPTY_ORDER) {
            val bundleObject = bundleOf(
                CURRENT_COURIER to currentCourier,
                CURRENT_ORDER to SharedResources.executor.getOrder()
            )
            findNavController().navigate(R.id.action_orderListFragment2_to_orderMapFragment, bundleObject)
        } else {
            dialog = ProgressDialog.show(
                requireContext(),
                "Fetching all orders list",
                "Loading ...")

            configureListView()
            fetchOrders()
        }
    }

    private fun configureListView() {
        arrayOfOrders = ArrayList()
        rvListOfOrders = requireView().findViewById(R.id.listViewOfOrders)
    }

    private fun fetchOrders() {
        FirebaseRDBService.executor.fetchAllOrders {
            rvListOfOrders.adapter = OrderListAdapter(
                requireContext(),
                it,
            object : ButtonChooseCallback {
                override fun configureMapForItem(order: Order) {
                    FirebaseRDBService.executor.setCourierOrder(order.orderNumber.toString(), currentCourier!!.login!!)

                    SharedResources.executor.setOrder(order)

                    val bundleObject = bundleOf(
                        CURRENT_COURIER to currentCourier,
                        CURRENT_ORDER to order
                    )

                   // findNavController().navigate(R.id.introFragment, bundleObject)
                }
            })
            arrayOfOrders = it
            dialog.dismiss()
        }
    }
}

interface ButtonChooseCallback {
    fun configureMapForItem(order: Order)
}
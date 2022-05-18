package com.example.graduationproject.ui.orders.orderList

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
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
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui_components.OnCourierItemClick

class OrderListFragment : Fragment() {

    companion object {
        fun newInstance() = OrderListFragment()
    }

    private lateinit var callback: OnCourierItemClick
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog = ProgressDialog.show(requireContext(), "Fetching all orders list", "Loading ...")
        currentCourier = arguments?.getParcelable(Constants.CURRENT_COURIER)!!
        configureListView()
        fetchOrders()
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
                    val bundleObject = bundleOf(Constants.CURRENT_ORDER to order, Constants.CURRENT_COURIER to currentCourier)

                        //findNavController().navigate(R.id.action_orderListFragment_to_mapFragment, bundleObject)
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
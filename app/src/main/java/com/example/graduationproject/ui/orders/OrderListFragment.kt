package com.example.graduationproject.ui.orders

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchCouriers
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.example.graduationproject.ui_components.CourierListAdapter
import com.example.graduationproject.ui_components.OnCourierItemClick

class OrderListFragment : Fragment() {

    companion object {
        fun newInstance() = OrderListFragment()
    }

    private lateinit var callback: OnCourierItemClick
    private lateinit var rvListOfOrders: ListView
    private lateinit var dialog: ProgressDialog
    private lateinit var viewModel: OrderListViewModel
    private lateinit var arrayOfOrders: ArrayList<Order>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)
        dialog = ProgressDialog.show(requireContext(), "Fetching all orders list", "Loading ...")
        configureListView()
        fetchOrders()
    }

    private fun configureListView() {
        arrayOfOrders = ArrayList()
        rvListOfOrders = requireView().findViewById(R.id.listViewOfOrders)
        rvListOfOrders.setOnItemClickListener { adapterView, view, i, l ->
            val bundleObject = bundleOf("CURRENT_ORDER" to arrayOfOrders[0])
            findNavController().navigate(R.id.action_orderListFragment_to_mapFragment, bundleObject)
        }
    }

    private fun fetchOrders() {
        FirebaseRDBService.executor.fetchAllOrders {
            rvListOfOrders.adapter = OrderListAdapter(
                requireContext(),
                it,
            object : ButtonChooseCallback {
                override fun configureMapForItem(order: Order) {
                    FirebaseRDBService.executor.setCourierOrder(order.orderNumber.toString(), "rmnvlshn123")
                    val bundleObject = bundleOf("CURRENT_ORDER" to order)
                    findNavController().navigate(R.id.action_orderListFragment_to_mapFragment, bundleObject)
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
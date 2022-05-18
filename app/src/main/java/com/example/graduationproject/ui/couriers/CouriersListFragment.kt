package com.example.graduationproject.ui.couriers

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
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchCouriers
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier

class CouriersListFragment : Fragment(){

    private lateinit var rvListOfCouriers: ListView
    private lateinit var dialog: ProgressDialog
    private lateinit var viewModel: CouriersListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.couriers_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[CouriersListViewModel::class.java]
        dialog = ProgressDialog.show(requireContext(), "Fetching couriers list", "Loading ...")
        rvListOfCouriers = requireView().findViewById(R.id.listViewOfCouriers)

        fetchCouriers()
    }

    private fun fetchCouriers() {
        FirebaseRDBService.executor.fetchCouriers(object : FetchCouriers {
            override fun onSuccessResponse(arrayList: ArrayList<Courier>) {

                rvListOfCouriers.adapter = CourierListAdapter(requireContext(), arrayList)

                rvListOfCouriers.setOnItemClickListener { adapterView, view, i, l ->
                    if(arrayList[i].order != "null") {
                        val currentCourier = arrayList[i]
                        val bundleCurrentCourier = bundleOf(Constants.CURRENT_COURIER to currentCourier)
                        findNavController().navigate(R.id.action_couriersListFragment2_to_courierMapFragment, bundleCurrentCourier)
                    } else {
                        Toast.makeText(requireContext(), "Кур'єр нічого не доставляє", Toast.LENGTH_LONG).show()
                    }
                }
                dialog.dismiss()
            }

            override fun onFailureResponse() {}
        })
    }

    private fun configureMonitoringButton() {

    }
}
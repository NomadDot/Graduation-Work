package com.example.graduationproject.ui.couriers

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchCouriers
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.model.Courier
import com.example.graduationproject.ui_components.CourierListAdapter
import com.example.graduationproject.ui_components.OnCourierItemClick

class CouriersListFragment : Fragment(){

    companion object {
        fun newInstance() = CouriersListFragment()
    }
    private lateinit var callback: OnCourierItemClick
    private lateinit var rvListOfCouriers: RecyclerView
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
        configureRecyclerView()
        fetchCouriers()
    }

    private fun configureRecyclerView() {
        callback = object : OnCourierItemClick {
            override fun onClickListener(position: Int) {
                Toast.makeText(requireContext(), position.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        rvListOfCouriers = requireView().findViewById(R.id.recyclerViewOfCouriers)
        rvListOfCouriers.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchCouriers() {
        FirebaseRDBService.executor.fetchCouriers(object : FetchCouriers {
            override fun onSuccessResponse(arrayList: ArrayList<Courier>) {
                rvListOfCouriers.adapter = CourierListAdapter(listOfCouriers = arrayList, callback)
                dialog.dismiss()
            }

            override fun onFailureResponse() {}
        })
    }
}
package com.example.graduationproject.ui.flows.admin_flow

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.graduationproject.R
import com.example.graduationproject.components.sharedResources.SharedResources
import kotlin.math.exp

class AdminMapFragment : Fragment() {

    private lateinit var viewModel: AdminMapViewModel
    private lateinit var expandableListView: ExpandableListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_completed_orders, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminMapViewModel::class.java)
        initView()

        val dialog = ProgressDialog.show(
            SharedResources.executor.getContext()!!,
            "Завантаження даних",
            null
        )

        viewModel.fetchCompletedOrders {
            dialog.dismiss()
            configureExpandableListView(it)
        }
    }

    private fun configureExpandableListView(adapter: CustomExpandableListAdapter) {
        expandableListView.setOnGroupExpandListener(object :
            ExpandableListView.OnGroupExpandListener {
            var previousGroup = -1
            override fun onGroupExpand(groupPosition: Int) {
                if (groupPosition != previousGroup) expandableListView.collapseGroup(previousGroup)
                previousGroup = groupPosition
            }
        })

        expandableListView.setAdapter(adapter)
    }

    private fun initView() {
        expandableListView = requireView().findViewById(R.id.expadableList)
    }

}
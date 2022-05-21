package com.example.graduationproject.ui.flows.courier_flow

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants
import com.example.graduationproject.core.Constants.Companion.EMPTY_ORDER
import com.example.graduationproject.model.Courier
import com.google.android.material.bottomnavigation.BottomNavigationView

class CourierFlowFragment : Fragment() {

    companion object {
        lateinit var courier: Courier
    }

    private lateinit var viewModel: CourierFlowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_courier_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val navController = (childFragmentManager.findFragmentById(R.id.fragmentContainerViewCourier) as NavHostFragment).navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CourierFlowViewModel::class.java)

        val currentCourier =  SharedResources.executor.getCourier()

        FirebaseRDBService.executor.fetchCurrentOrder(currentCourier!!.order.toString()) { order ->
            SharedResources.executor.setOrder(order!!)
        }
        setOnBackButtonPressListener()
    }

    private fun setOnBackButtonPressListener() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }
}
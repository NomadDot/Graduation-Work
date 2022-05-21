package com.example.graduationproject.ui.flows.admin_flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.graduationproject.R
import com.example.graduationproject.core.Constants
import com.example.graduationproject.core.Constants.Companion.CURRENT_COURIER
import com.example.graduationproject.core.Constants.Companion.CURRENT_ORDER
import com.example.graduationproject.model.Courier
import com.example.graduationproject.model.Order
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFlowFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courier = arguments?.getParcelable<Courier>(CURRENT_COURIER)
        val order = arguments?.getParcelable<Order>(CURRENT_ORDER)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val navController = (childFragmentManager.findFragmentById(R.id.fragmentContainerViewAdmin) as NavHostFragment).navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profileFragment2 -> {
                    val bundledCourier = bundleOf(CURRENT_COURIER to courier)
                    destination.addInDefaultArgs(bundledCourier)
                }
            }
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

}
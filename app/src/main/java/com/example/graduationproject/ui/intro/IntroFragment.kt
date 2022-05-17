package com.example.graduationproject.ui.intro

import android.Manifest
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchUserCallback
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.Courier

class IntroFragment : Fragment() {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: IntroViewModel

    private lateinit var btnAuth: Button
    private lateinit var btnRegister: Button
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText

    private lateinit var dialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    private fun initView() {
        btnAuth = requireView().findViewById(R.id.btnAuth)
        btnRegister = requireView().findViewById(R.id.btnRegister)
        etLogin = requireView().findViewById(R.id.etLogin)
        etPassword = requireView().findViewById(R.id.etPassword)
        test()
    }

    private fun configureRegisterButton() {
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_registerCourierFragment)
        }
    }

    private fun configureAuthButton() {
        btnAuth.setOnClickListener {
            dialog = ProgressDialog.show(requireContext(), "Authorization", "Loading ...")

            FirebaseRDBService.executor.authenticateUser(
                etLogin.text.toString(),
                object : FetchUserCallback {
                    override fun onSuccessResponse(courier: Courier) {
                        configureFlowWithUserInfo(courier)
                    }

                    override fun onFailureResponse() {
                         Toast.makeText(
                                requireContext(),
                        "Error. Something went wrong ",
                        Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    private fun configureFlowWithUserInfo(courier: Courier) {
        if (etPassword.text.toString() == courier.password) {
            if (courier.password == "admin") {

                Toast.makeText(
                    requireContext(),
                    "You've been successfully authorized.",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()

                val currentCourier = bundleOf( Constants.CURRENT_COURIER to courier)

                findNavController().navigate(R.id.action_introFragment_to_couriersListFragment, currentCourier)
            } else {
                if (courier.order == "null") {

                    Toast.makeText(
                        requireContext(),
                        "You've been successfully authorized.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()

                    val currentCourier = bundleOf( Constants.CURRENT_COURIER to courier)
                    findNavController().navigate(R.id.action_introFragment_to_orderListFragment, currentCourier)

                } else {
                    FirebaseRDBService.executor.fetchCurrentOrder(courier.order!!) {

                        Toast.makeText(
                            requireContext(),
                            "You've been successfully authorized.",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()

                        val order = it
                        val bundleObject = bundleOf(
                            Constants.CURRENT_ORDER to order,
                            Constants.CURRENT_COURIER to courier
                        )
                        findNavController().navigate(R.id.action_introFragment_to_mapFragment, bundleObject)
                    }
                }
            }
        } else {

            Toast.makeText(
                requireContext(),
                "Error. Password or Login is not correct or courier is not exist ",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]
        initView()
        configureAuthButton()
        configureRegisterButton()

        requireActivity().requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,), 0)
    }

    private fun test() {
        etLogin.setText("rmnvlshn123")
        etPassword.setText("123")
    }

}
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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FetchUserCallback
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.model.Courier
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class IntroFragment : Fragment() {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: IntroViewModel

    private lateinit var btnAuth: Button
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    fun initView() {
        btnAuth = requireView().findViewById(R.id.btnAuth)
        etLogin = requireView().findViewById(R.id.etLogin)
        etPassword = requireView().findViewById(R.id.etPassword)
        test()
    }

    fun configureAuthButton() {
        btnAuth.setOnClickListener {
            val dialog = ProgressDialog.show(requireContext(), "Authorization", "Loading ...")

            FirebaseRDBService.executor.authenticateUser(
                etLogin.text.toString(),
                object : FetchUserCallback {
                    override fun onSuccessResponse(courier: Courier) {
                        if (etPassword.text.toString() == courier.password) {
                            Toast.makeText(
                                    requireContext(),
                            "You've been successfully authorized.",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()

                            findNavController().navigate(R.id.action_introFragment_to_mapFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error. Password or Login is not correct or courier is not exist ",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                    }

                    override fun onFailureResponse() {
                         Toast.makeText(
                                requireContext(),
                        "Error. Password or Login is not correct or courier is not exist ",
                        Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]
        initView()
        configureAuthButton()

        requireActivity().requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,), 0)
    }

    fun test() {
        etLogin.setText("rmnvlshn123")
        etPassword.setText("123")
    }

}
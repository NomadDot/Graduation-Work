package com.example.graduationproject.ui.intro

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]

        val dialog = ProgressDialog.show(requireContext(), "Loading", null)
        FirebaseRDBService.executor.authenticateUser("1234", object : FetchUserCallback {
            override fun onSuccessResponse(courier: Courier) {
                Toast.makeText(
                    requireContext(),
                    "You've been successfully authorized.",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }

            override fun onFailureResponse() {
                Toast.makeText(
                    requireContext(),
                    "Error. Password or Login is not correct or courier is not exist ",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        })
    }

}
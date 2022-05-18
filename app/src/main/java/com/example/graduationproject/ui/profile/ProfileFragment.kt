package com.example.graduationproject.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.core.Constants

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val btnLogout = requireView().findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val preferences = requireContext().getSharedPreferences(Constants.USER_DATA_STORAGE, Context.MODE_PRIVATE)
            preferences.edit().clear().apply()
            findNavController().popBackStack(R.id.introFragment, false)
        }
    }

}
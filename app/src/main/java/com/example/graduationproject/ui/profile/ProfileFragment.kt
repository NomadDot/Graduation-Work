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
import android.widget.TextView
import com.example.graduationproject.R
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("CommitPrefEdits", "SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val courier = SharedResources.executor.getCourier()

        val tvName = requireView().findViewById<TextView>(R.id.tvCourierName)
        val tvAge = requireView().findViewById<TextView>(R.id.tvCourierAge)

        tvName.text = "${courier!!.name} ${courier.lastName}"
        tvAge.text = courier.age

        val btnLogout = requireView().findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val preferences =
                SharedResources.executor.getContext()!!.getSharedPreferences(
                    Constants.USER_DATA_STORAGE, Context.MODE_PRIVATE
                )

            preferences.edit().clear().apply()
            requireActivity().finish()
        }
    }
}
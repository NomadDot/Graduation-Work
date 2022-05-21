package com.example.graduationproject.ui.intro

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.model.Courier

class RegisterCourierFragment : Fragment() {

    companion object {
        val EMPTY_VALUE = ""
    }

    private lateinit var viewModel: RegisterCourierViewModel

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etAge: EditText
    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText

    private lateinit var btnRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_courier, container, false)
    }

    fun initView() {
        btnRegister = requireView().findViewById(R.id.btnRegister)
        etName = requireView().findViewById(R.id.etName)
        etLastName = requireView().findViewById(R.id.etLastName)
        etPhoneNumber = requireView().findViewById(R.id.etPhoneNumber)
        etAge = requireView().findViewById(R.id.etAge)
        etLogin = requireView().findViewById(R.id.etLogin)
        etPassword = requireView().findViewById(R.id.etPassword)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterCourierViewModel::class.java)
        initView()
        configureRegisterButton()
    }

    private fun configureRegisterButton() {
        btnRegister.setOnClickListener {
            val courierValue = getValuesFromFields()

            if(
                courierValue.name != EMPTY_VALUE &&
                courierValue.lastName != EMPTY_VALUE &&
                courierValue.age != EMPTY_VALUE &&
                courierValue.phoneNumber != EMPTY_VALUE &&
                courierValue.login != EMPTY_VALUE &&
                courierValue.password != EMPTY_VALUE
            ) {
                val courier = Courier(
                    name = courierValue.name,
                    lastName = courierValue.lastName,
                    age = courierValue.age,
                    phoneNumber = courierValue.phoneNumber,
                    login = courierValue.login,
                    password = courierValue.password,
                    order = courierValue.order
                )
                FirebaseRDBService.executor.insertUserValueToDatabase(courier)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getValuesFromFields(): CourierValues {
        return CourierValues(
            name = etName.text.toString(),
            lastName = etLastName.text.toString(),
            age = etAge.text.toString(),
            phoneNumber = etPhoneNumber.text.toString(),
            login = etLogin.text.toString(),
            password = etPassword.text.toString()
        )
    }
}

data class CourierValues(
    var name: String? = null,
    var lastName: String? = null,
    var age: String? = null,
    var phoneNumber: String? = null,
    var login: String? = null,
    var password: String? = null,
    var order: String = "null"
)
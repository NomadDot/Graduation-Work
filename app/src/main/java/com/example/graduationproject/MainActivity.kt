package com.example.graduationproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService
import com.example.graduationproject.components.sharedResources.SharedResources
import com.example.graduationproject.core.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseRDBService.executor.configureFirebaseDatabase()
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        val preferences = getSharedPreferences(Constants.USER_DATA_STORAGE, MODE_PRIVATE)
        preferences.edit().putBoolean(Constants.IS_APP_LAUNCH_ONCE, false).apply()
    }
}
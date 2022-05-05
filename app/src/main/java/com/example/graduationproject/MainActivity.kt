package com.example.graduationproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.graduationproject.components.FirebaseRDBService.FirebaseRDBService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseRDBService.executor.configureFirebaseDatabase()
        setContentView(R.layout.activity_main)
    }
}
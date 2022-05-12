package com.example.graduationproject.components.location_utils

import android.content.Context
import android.util.Log
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class GoogleDirectionAPI {
    companion object {

        /* Directions API */
        fun fetchRoute(
            origin : String,
            destination: String,
            context: Context,
            callback: (Response<DirectionResponses>) -> Unit
        ) {
            val apiServices = RetrofitClient.apiServices(context)

            apiServices.getDirection(origin, destination, Constants.ApiKey)
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                        Log.i("Working: ", response.body().toString())
                        callback.invoke(response)
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        t.localizedMessage?.let { Log.i("error", it) }
                    }
                })
        }
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(context: Context): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build()

            return retrofit.create<ApiServices>(ApiServices::class.java)
        }
    }
}
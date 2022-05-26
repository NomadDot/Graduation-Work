package com.example.graduationproject.components.locationServices

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.example.graduationproject.core.Constants
import com.example.graduationproject.model.DirectionResponses
import com.example.graduationproject.model.Distance
import com.example.graduationproject.model.Leg
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
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

        fun fetchDistance(
                origin : String,
                destination: String,
                context: Context,
                callback: (String) -> Unit
            ) {
            val apiServices = RetrofitClient.apiServices(context)

            apiServices.getDirection(origin, destination, Constants.ApiKey)
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(
                        call: Call<DirectionResponses>,
                        response: Response<DirectionResponses>
                    ) {
                        Log.i("Working: ", response.body()!!.routes!![0]!!.legs!![0]!!.distance!!.toString())
                        response.body()?.let {
                            it.routes?.let {
                                callback.invoke(it[0]!!.legs!![0]!!.distance!!.value.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        t.localizedMessage?.let { Log.i("error", it) }
                    }

                })
        }

        fun fetchDuration(
            origin : String,
            destination: String,
            context: Context,
            callback: (String) -> Unit
        ) {
            val apiServices = RetrofitClient.apiServices(context)

            apiServices.getDirection(origin, destination, Constants.ApiKey)
                .enqueue(object : Callback<DirectionResponses> {
                    override fun onResponse(
                        call: Call<DirectionResponses>,
                        response: Response<DirectionResponses>
                    ) {
                        Log.i("Working: ", response.body()!!.routes!![0]!!.legs!![0]!!.duration!!.text!!)
                        response.body()?.let {
                            it.routes?.let {
                                callback.invoke(it[0]!!.legs!![0]!!.duration!!.text!!)
                            }
                        }
                    }

                    override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                        t.localizedMessage?.let { Log.i("error", it) }
                    }
                })
        }


        fun drawPolyline(response: Response<DirectionResponses>, map: GoogleMap) {

            val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points

            val polyline = PolylineOptions()
                .addAll(PolyUtil.decode(shape))
                .width(8f)
                .color(Color.RED)
                .geodesic(true)
                .clickable(true)

            map.addPolyline(polyline)
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
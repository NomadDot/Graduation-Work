package com.example.graduationproject.components.locationServices

import com.beust.klaxon.*
import com.example.graduationproject.core.Constants
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import java.io.ByteArrayInputStream

class GoogleMapsGeocodingRequest {

    private class ResponseError(
        val statusCode: Int,
        val statusMessage: String,
        val errorMessage: String
    ) : RuntimeException("[%d - %s] %s".format(statusCode, statusMessage, errorMessage))

    private val apiURL = "https://maps.googleapis.com/maps/api/geocode/json"

    private val googleAPIKey = Constants.ApiKey // your APIKey

    /**
     * @param searchString the string to geocode
     */
    fun geocode(searchString: String, callback: (GoogleMapsGeocodingParser.ParsedResponse?) -> Unit): GoogleMapsGeocodingParser.ParsedResponse? {

        val (_, _, result) = "$apiURL?address=$searchString=&key=$googleAPIKey".httpGet().responseString()

        var data: GoogleMapsGeocodingParser.ParsedResponse? = null

        when (result) {
            is Result.Failure -> onError(result)
            is Result.Success -> data = GoogleMapsGeocodingParser.parse(result.getAs<String>()!!)
        }
        callback.invoke(data)
        return data
    }

    private fun onError(failureResult: Result.Failure<FuelError>) {

        throw ResponseError(
            statusCode = failureResult.error.response.statusCode,
            statusMessage = failureResult.error.response.responseMessage,
            errorMessage = failureResult.getErrorMessage())
    }

    private fun Result.Failure<FuelError>.getErrorMessage(): String {

        return try {

            val errorDataInputStream = ByteArrayInputStream(this.error.errorData, 0, this.error.errorData.size)
            val jsonErrorData: JsonObject = Parser().parse(errorDataInputStream) as JsonObject

            jsonErrorData.string("error_message")!!

        } catch (e: RuntimeException) {
            ""
        }
    }
}

object GoogleMapsGeocodingParser {

    data class Coordinates(val lat: Double, val lng: Double)

    data class Country(val name: String, val isoCode: String)

    data class ParsedResponse(
        val address: String,
        val coordinates: Coordinates,
        val country: Country
    )

    private val allowedTyps = setOf(
        "street_address",
        "route",
        "country",
        "locality",
        "administrative_area_level_1",
        "administrative_area_level_2",
        "administrative_area_level_3",
        "administrative_area_level_4",
        "administrative_area_level_5",
        "ward",
        "airport")

    fun parse(googleMapsResponse: String): ParsedResponse? {

        val jsonResponse: JsonObject = googleMapsResponse.toJsonObject()

        return if (jsonResponse.string("status") != "OK")

            null

        else try {

            jsonResponse.array<JsonObject>("results")!!
                .first { it.array<String>("types")!!.toSet().intersect(this.allowedTyps).isNotEmpty() }
                .toParsedResponse()

        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun String.toJsonObject(): JsonObject {

        val byteArray: ByteArray = this.toByteArray()

        return Parser().parse(ByteArrayInputStream(byteArray)) as JsonObject
    }

    private fun JsonObject.toParsedResponse(): ParsedResponse {

        val coordinates: JsonObject = this.obj("geometry")!!.obj("location")!!
        val country: JsonObject = this.array<JsonObject>("address_components")!!.first {
            "country" in it.array<String>("types")!!
        }

        return ParsedResponse(
            address = this.string("formatted_address")!!,
            coordinates = Coordinates(lat = coordinates.double("lat")!!, lng = coordinates.double("lng")!!),
            country = Country(name = country.string("long_name")!!, isoCode = country.string("short_name")!!.toLowerCase())
        )
    }
}
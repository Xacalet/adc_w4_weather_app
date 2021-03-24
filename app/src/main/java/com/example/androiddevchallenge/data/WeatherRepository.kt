/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.WeatherType
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class WeatherRepository {

    fun getWeatherSummary(): ForecastSummary {
        return ForecastSummary(
            weatherType = WeatherType.PartlyCloudy,
            temperature = 23f,
            chanceOfPrecipitation = 5f,
            windSpeed = 20f,
            humidity = 60f,
            location = "Barcelona, Catalunya"
        )
    }

    fun getForecastByHour(): List<ForecastDataItem> {
        return forecastByHourData
    }

    fun getForecastForNextSevenDays(): List<ForecastDataItem> {
        return forecastForNextSevenDays
    }
}

private val forecastByHourData = (0..23).map { hour ->
    ForecastDataItem(
        weatherType = when (hour % 4) {
            0 -> WeatherType.Sunny
            1 -> WeatherType.PartlyCloudy
            2 -> WeatherType.Cloudy
            else -> WeatherType.PartlyCloudy
        },
        temperature = 20f + (hour % 3),
        chanceOfPrecipitation = when (hour % 6) {
            0 -> 10f
            1 -> 29f
            2 -> 63f
            3 -> 100f
            4 -> 81f
            5 -> 45f
            else -> 15f
        },
        datetime = now().withHour(hour),
    )
}

private val forecastForNextSevenDays = (0..6).map { day ->
    ForecastDataItem(
        weatherType = if (day % 2 == 0) WeatherType.PartlyCloudy else WeatherType.Cloudy,
        temperature = 15f + (day % 7),
        chanceOfPrecipitation = when (day) {
            1 -> 0f
            2 -> 10f
            3 -> 19f
            4 -> 34f
            5 -> 30f
            6 -> 5f
            else -> 10f
        },
        datetime = now().plusDays(day.toLong())
    )
}

data class ForecastDataItem(
    val weatherType: WeatherType,
    val temperature: Float,
    val chanceOfPrecipitation: Float,
    val datetime: LocalDateTime
)

data class ForecastSummary(
    val weatherType: WeatherType,
    val temperature: Float,
    val chanceOfPrecipitation: Float,
    val windSpeed: Float,
    val humidity: Float,
    val location: String
)

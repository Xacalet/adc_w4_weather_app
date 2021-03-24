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
package com.example.androiddevchallenge

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.data.ForecastSummary
import com.example.androiddevchallenge.data.WeatherRepository
import com.example.androiddevchallenge.ui.foundations.ChartData
import com.example.androiddevchallenge.ui.foundations.WeatherBeanData
import java.time.LocalDateTime

class MainViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _forecastSummary = MutableLiveData<ForecastSummary>(null)
    val forecastSummary: LiveData<ForecastSummary> = _forecastSummary

    private val _forecastByTimeRange = MutableLiveData<ForecastByTimeRangeUiData>(null)
    val forecastByTimeRange: LiveData<ForecastByTimeRangeUiData> = _forecastByTimeRange

    init {
        loadData(TimeRange.ByHour)
        _forecastSummary.value = weatherRepository.getWeatherSummary()
    }

    fun setTimeRange(timeRange: TimeRange) {
        loadData(timeRange)
    }

    private fun loadData(timeRange: TimeRange) {
        (
            if (timeRange == TimeRange.ByHour) {
                weatherRepository.getForecastByHour()
            } else {
                weatherRepository.getForecastForNextSevenDays()
            }
            ).let { rowList ->
            val weatherBeans = rowList.map { rowItem ->
                WeatherBeanData(
                    upperText = formatDataTime(rowItem.datetime, timeRange),
                    weatherType = rowItem.weatherType,
                    lowerText = "${rowItem.temperature.toInt()}ºC",
                    isSelected = if (timeRange == TimeRange.ByHour) {
                        LocalDateTime.now().hour == rowItem.datetime.hour
                    } else {
                        LocalDateTime.now().dayOfYear == rowItem.datetime.dayOfYear
                    }
                )
            }
            val chartData = ChartData(
                values = rowList.map { rowItem -> rowItem.chanceOfPrecipitation }
                    .filterIndexed { i, _ -> timeRange != TimeRange.ByHour || i % 4 == 0 },
                tags = rowList.map { rowItem -> formatDataTime(rowItem.datetime, timeRange) }
            )
            _forecastByTimeRange.value = ForecastByTimeRangeUiData(weatherBeans, chartData)
        }
    }

    private fun formatDataTime(datetime: LocalDateTime, timeRange: TimeRange) =
        if (timeRange == TimeRange.ByHour) {
            "%02d:00".format(datetime.hour)
        } else {
            "%02d.%02d".format(datetime.monthValue, datetime.dayOfMonth)
        }
}

data class ForecastByTimeRangeUiData(
    val weatherBeans: List<WeatherBeanData>,
    val chartData: ChartData
)

sealed class TimeRange {
    object ByHour : TimeRange()
    object NextSevenDays : TimeRange()
}

sealed class WeatherType(
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    @RawRes val animatedIcon: Int
) {
    object Cloudy : WeatherType(
        name = R.string.cloudy,
        icon = R.drawable.ic_weather_cloudy,
        animatedIcon = R.raw.lottie_weather_cloudy
    )

    object LightningRainy : WeatherType(
        name = R.string.lightning_rainy,
        icon = R.drawable.ic_weather_lightning_rainy,
        animatedIcon = R.raw.lottie_weather_stormy
    )

    object PartlyCloudy : WeatherType(
        R.string.partly_cloudy,
        icon = R.drawable.ic_weather_partly_cloudy,
        animatedIcon = R.raw.lottie_weather_partly_cloudy
    )

    object Pouring : WeatherType(
        name = R.string.pouring,
        icon = R.drawable.ic_weather_pouring,
        animatedIcon = R.raw.lottie_weather_pouring
    )

    object Rainy : WeatherType(
        name = R.string.rainy,
        icon = R.drawable.ic_weather_rainy,
        animatedIcon = R.raw.lottie_weather_rainy
    )

    object Sunny : WeatherType(
        name = R.string.sunny,
        icon = R.drawable.ic_weather_sunny,
        animatedIcon = R.raw.lottie_weather_sunny
    )

    object Snowy : WeatherType(
        name = R.string.snowy,
        icon = R.drawable.ic_weather_snowy,
        animatedIcon = R.raw.lottie_weather_snowy
    )
}

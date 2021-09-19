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
package com.example.androiddevchallenge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.androiddevchallenge.ForecastByTimeRangeUiData
import com.example.androiddevchallenge.MainViewModel
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.TimeRange
import com.example.androiddevchallenge.data.ForecastSummary
import com.example.androiddevchallenge.ui.foundations.*
import com.example.androiddevchallenge.ui.theme.Lato
import com.example.androiddevchallenge.ui.theme.Montserrat
import com.example.androiddevchallenge.ui.theme.rainColor
import com.example.androiddevchallenge.ui.theme.temperatureColor
import kotlin.math.roundToInt

@Preview
@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        val viewModel = viewModel<MainViewModel>()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            viewModel.forecastSummary.observeAsState().value?.let { data ->
                ForecastSummary(data)
            }
            viewModel.forecastByTimeRange.observeAsState().value?.let { data ->
                ForecastByTimeRange(data) {
                    viewModel.setTimeRange(it)
                }
            }
        }
    }
}

@Composable
fun ForecastSummary(
    data: ForecastSummary
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(data.weatherType.animatedIcon))
    Box {
        RoundedHeaderBackground(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(),
            color = colors.primary,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CompositionLocalProvider(LocalContentColor provides colors.primary) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(colors.background),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier.size(100.dp),
                        iterations = LottieConstants.IterateForever
                    )
                    Spacer(Modifier.size(24.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 72.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Light
                                )
                            ) { append("${data.temperature.roundToInt()}") }
                            withStyle(
                                SpanStyle(
                                    fontSize = 56.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Black,
                                    baselineShift = BaselineShift(0.8f)
                                )
                            ) {
                                append("º")
                            }
                            withStyle(
                                SpanStyle(
                                    fontSize = 48.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Black,
                                    baselineShift = BaselineShift(1f)
                                )
                            ) {
                                append("C")
                            }
                        }
                    )
                }
            }

            CompositionLocalProvider(LocalContentColor provides colors.onPrimary) {
                Spacer(Modifier.size(8.dp))
                Text(
                    text = data.location,
                    style = typography.h1
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = stringResource(data.weatherType.name).uppercase(),
                    style = typography.subtitle1.copy(letterSpacing = 1.sp)
                )
                Spacer(Modifier.size(24.dp))
                Column(Modifier.fillMaxWidth()) {
                    (1..3).forEach {
                        val labelText = when (it) {
                            1 -> "Chance of rain: "
                            2 -> "Humidity: "
                            else -> "Wind speed: "
                        }
                        val valueText = when (it) {
                            1 -> "${data.chanceOfPrecipitation.roundToInt()}%"
                            2 -> "${data.humidity.roundToInt()}%"
                            else -> "${data.windSpeed.roundToInt()} kph"
                        }
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontFamily = Lato,
                                        fontWeight = FontWeight.Normal
                                    )
                                ) { append(labelText) }
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontFamily = Lato,
                                        fontWeight = FontWeight.Black
                                    )
                                ) {
                                    append(valueText)
                                }
                            }
                        )

                        Spacer(Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.ForecastByTimeRange(
    data: ForecastByTimeRangeUiData,
    onSetTimeRange: (TimeRange) -> Unit
) {
    var selectedTab by remember { mutableStateOf(TabPage.ByHour) }
    Column(
        modifier = Modifier.align(Alignment.BottomCenter),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth(.75f)
                .border(2.dp, colors.primary, RoundedCornerShape(25f)),
            indicator = { tabPositions ->
                TabIndicator(
                    modifier = Modifier.clip(RoundedCornerShape(25f)),
                    tabPositions = tabPositions,
                    tab = selectedTab
                )
            },
            tabs = {
                Tab(
                    text = stringResource(R.string.by_hour),
                    onClick = {
                        onSetTimeRange(TimeRange.ByHour)
                        selectedTab = TabPage.ByHour
                    },
                    isTabSelected = selectedTab == TabPage.ByHour
                )
                Tab(
                    text = stringResource(R.string.next_seven_days),
                    onClick = {
                        onSetTimeRange(TimeRange.NextSevenDays)
                        selectedTab = TabPage.NextSevenDays
                    },
                    isTabSelected = selectedTab == TabPage.NextSevenDays
                )
            }
        )
        Spacer(Modifier.size(24.dp))
        Row {
            Box(Modifier.border(1.dp, temperatureColor, CircleShape)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_thermometer),
                    contentDescription = null,
                    tint = temperatureColor
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            WeatherPod(Modifier.weight(1f), data.weatherBeans)
        }
        Spacer(Modifier.size(16.dp))
        Row {
            Box(Modifier.border(1.dp, rainColor, CircleShape)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_drop),
                    contentDescription = null,
                    tint = rainColor
                )
            }
            Spacer(Modifier.size(8.dp))
            Chart(
                modifier = Modifier
                    .height(150.dp)
                    .weight(1f),
                chartData = data.chartData,
                chartConfiguration = ChartConfiguration(
                    markerColor = colors.background,
                    axisColor = colors.onBackground,
                    areaColor = rainColor,
                    formatValue = { it?.let { "${it.roundToInt()}%" } ?: "" },
                    valueRange = ChartValueRange.CoerceAt(0f, 100f)
                )
            )
        }
    }
}

enum class TabPage {
    ByHour, NextSevenDays
}

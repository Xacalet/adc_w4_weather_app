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
package com.example.androiddevchallenge.ui.foundations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.WeatherType
import com.example.androiddevchallenge.ui.theme.WeatherAppTheme
import com.example.androiddevchallenge.ui.theme.temperatureColor
import com.example.androiddevchallenge.ui.theme.typography

data class WeatherBeanData(
    val upperText: String,
    val weatherType: WeatherType,
    val lowerText: String,
    val isSelected: Boolean = false,
)

@Composable
fun WeatherBean(
    modifier: Modifier = Modifier,
    data: WeatherBeanData
) {
    val backgroundColor = if (data.isSelected) temperatureColor else colors.background
    val contentColor = if (data.isSelected) colors.onPrimary else colors.onBackground
    Column(
        modifier = modifier
            .height(150.dp)
            .width(75.dp)
            .clip(RoundedCornerShape(50))
            .border(
                brush = Brush.verticalGradient(
                    listOf(
                        temperatureColor.copy(alpha = 0.5f),
                        temperatureColor.copy(alpha = 0.1f)
                    )
                ),
                width = if (data.isSelected) 0.dp else 2.dp,
                shape = RoundedCornerShape(50)
            )
            .background(backgroundColor),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CompositionLocalProvider(LocalContentColor provides contentColor) {

            Text(data.upperText, style = typography.body1)
            Icon(
                modifier = Modifier.size(42.dp),
                painter = painterResource(data.weatherType.icon),
                contentDescription = null
            )
            Text(data.lowerText, style = typography.body2)
        }
    }
}

@Composable
fun AnimatedWeatherBean(modifier: Modifier = Modifier, data: WeatherBeanData) {
    val rotationY = remember { Animatable(0f) }
    val firstLaunch = remember { mutableStateOf(true) }
    val data1 = remember { mutableStateOf(data) }
    val data2 = remember { mutableStateOf(data) }

    LaunchedEffect(data) {
        if (!firstLaunch.value) {
            val rotateTo = ((rotationY.value / 180f).toInt() + 1) * 180f
            rotationY.animateTo(
                targetValue = rotateTo,
                animationSpec = tween(durationMillis = 1000)
            )
        }
        firstLaunch.value = false
    }

    Box {
        val reverseVisible = rotationY.value % 360f in (90f..270f)
        if (reverseVisible) {
            data1.value = data
            WeatherBean(
                modifier.graphicsLayer(rotationY = rotationY.value + 180f),
                data = data2.value
            )
        } else {
            data2.value = data
            WeatherBean(
                modifier = modifier.graphicsLayer(rotationY = rotationY.value),
                data = data1.value
            )
        }
    }

    /*Box(Modifier.graphicsLayer(rotationY = rotationY.value)) {
        val innerRotationY = if (rotationY.value % 360f in (90f..270f)) 180f else 0f
        WeatherBean(modifier.graphicsLayer(rotationY = innerRotationY), data)
    }*/
}

@Composable
@Preview(showBackground = true)
fun WeatherBeanPreview() {
    WeatherAppTheme {
        Surface(color = colors.background) {
            Row {
                WeatherBean(
                    Modifier,
                    WeatherBeanData(
                        upperText = "NOW",
                        weatherType = WeatherType.PartlyCloudy,
                        lowerText = "32ºC",
                        isSelected = true,
                    )
                )
                Spacer(Modifier.size(4.dp))
                WeatherBean(
                    Modifier,
                    WeatherBeanData(
                        upperText = "03:00",
                        weatherType = WeatherType.Sunny,
                        lowerText = "24ºC",
                        isSelected = false,
                    )
                )
                Spacer(Modifier.size(4.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AnimatedWeatherBeanPreview() {
    val templateData = WeatherBeanData(
        upperText = "NOW",
        weatherType = WeatherType.PartlyCloudy,
        lowerText = "32ºC",
        isSelected = true,
    )

    WeatherAppTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = colors.background
        ) {
            val data = remember { mutableStateOf(templateData.copy()) }
            val changingValue = remember { mutableStateOf(32) }
            Column {
                AnimatedWeatherBean(data = data.value)
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = {
                        changingValue.value = changingValue.value + 1
                        data.value = templateData.copy(
                            lowerText = "${changingValue.value} ºC",
                            isSelected = !data.value.isSelected
                        )
                    }
                ) {
                    Text("Change data")
                }
            }
        }
    }
}

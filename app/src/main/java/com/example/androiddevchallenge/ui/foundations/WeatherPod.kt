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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.WeatherType
import com.example.androiddevchallenge.ui.theme.WeatherAppTheme

@Composable
fun WeatherPod(
    modifier: Modifier = Modifier,
    data: List<WeatherBeanData>
) {
    val listState = rememberLazyListState()
    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(start = 8.dp)
    ) {
        items(data.size, key = { it }) { i ->
            AnimatedWeatherBean(Modifier, data[i])
            Spacer(Modifier.size(8.dp))
        }
    }
}

@Preview
@Composable
fun WeatherPodPreview() {
    val data = listOf(
        WeatherBeanData("01:00", WeatherType.Sunny, "24º"),
        WeatherBeanData("02:00", WeatherType.Rainy, "24º", true),
        WeatherBeanData("NOW", WeatherType.Snowy, "16º", true),
        WeatherBeanData("04:00", WeatherType.Cloudy, "28º"),
        WeatherBeanData("05:00", WeatherType.LightningRainy, "19º"),
    )
    WeatherAppTheme {
        WeatherPod(Modifier, data)
    }
}

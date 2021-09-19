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

import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.androiddevchallenge.R

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    chartData: ChartData,
    chartConfiguration: ChartConfiguration
) {
    // Set maximum and minimum values from the provided data
    val (maxValue, minValue) = when (chartConfiguration.valueRange) {
        ChartValueRange.ByData -> Pair(
            chartData.values.maxOrNull() ?: 1f,
            chartData.values.minOrNull() ?: 0f
        )
        is ChartValueRange.CoerceAt -> Pair(
            chartConfiguration.valueRange.max,
            chartConfiguration.valueRange.min
        )
    }

    // Generates an animation for each data point, starting at 0 and increasing simultaneously
    // until reaching the actual value.
    val animatedValues = chartData.values.map { remember { Animatable(minValue) } }
    animatedValues.forEachIndexed { index, animatable ->
        LaunchedEffect(chartData) {
            animatable.animateTo(chartData.values[index], tween(durationMillis = 1500))
        }
    }

    // Get typeface for chart tags (context cannot be retrieved inside a Canvas scope).
    val typeface: Typeface? = ResourcesCompat.getFont(LocalContext.current, R.font.lato)

    Canvas(modifier) {

        val sideMargin = size.width * 0.1f
        val xDiff: Float = size.width.minus(sideMargin * 2) / (chartData.values.size - 1)

        // Calculate margins inside the chart
        val topMargin = (size.height * 0.05f)
        val bottomMargin = (size.height * 0.95f)

        // Calculate ratio data/axis coordinates.
        val ratio =
            (bottomMargin - topMargin) / if (maxValue == minValue) 1f else (maxValue - minValue)

        // Calculates X,Y points based on
        val points = animatedValues.mapIndexed { index, value ->
            Offset((xDiff * index) + sideMargin, topMargin + (maxValue - value.value) * ratio)
        }.toMutableList().apply {
            add(0, Offset(0f, bottomMargin))
            add(Offset(this@Canvas.size.width, bottomMargin))
        }

        // Calculate control points for the Bezier curves
        val controlPoints1 = mutableListOf<PointF>()
        val controlPoints2 = mutableListOf<PointF>()
        for (i in 1 until points.size) {
            controlPoints1.add(PointF((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
            controlPoints2.add(PointF((points[i].x + points[i - 1].x) / 2, points[i].y))
        }

        drawLine(
            color = chartConfiguration.axisColor.copy(alpha = 0.2f),
            start = Offset(0f, size.height * 0.05f),
            end = Offset(size.width, size.height * 0.05f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f))
        )
        drawLine(
            color = chartConfiguration.axisColor.copy(alpha = 0.2f),
            start = Offset(0f, size.height * 0.5f),
            end = Offset(size.width, size.height * 0.5f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f))
        )

        // Draw chart area
        drawPath(
            color = chartConfiguration.areaColor,
            path = Path().apply {
                moveTo(0f, size.height)
                lineTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    val cp1 = controlPoints1[i - 1]
                    val cp2 = controlPoints2[i - 1]
                    cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, points[i].x, points[i].y)
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
            }
        )

        // Draw markers and tags
        for (i in 1 until points.size - 1) {
            drawCircle(
                color = chartConfiguration.markerColor,
                center = points[i],
                radius = 4.dp.toPx()
            )
            drawCircle(
                color = chartConfiguration.axisColor,
                center = points[i],
                radius = 4.dp.toPx(),
                style = Stroke(2f)
            )
            // TODO: Use native canvas until no drawText method is provided in DrawScope.
            val paint = Paint()
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 14.sp.toPx()
            paint.color = chartConfiguration.axisColor.toArgb()
            typeface?.let { paint.typeface = it }
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    chartConfiguration.formatValue(animatedValues.getOrNull(i - 1)?.value),
                    points[i].x,
                    points[i].y - 30f,
                    paint
                )
            }
        }

        // Draw axes
        drawLine(
            color = chartConfiguration.axisColor.copy(alpha = 0.2f),
            start = Offset(0f, size.height),
            end = Offset(0f, 0f)
        )
        drawLine(
            color = chartConfiguration.axisColor,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height)
        )
        drawLine(
            color = chartConfiguration.axisColor.copy(alpha = 0.2f),
            start = Offset(size.width, size.height),
            end = Offset(size.width, 0f)
        )
    }
}

sealed class ChartValueRange {
    object ByData : ChartValueRange()
    class CoerceAt(val min: Float, val max: Float) : ChartValueRange()
}

data class ChartData(
    val values: List<Float>,
    val tags: List<String>
)

data class ChartConfiguration(
    val markerColor: Color,
    val axisColor: Color,
    val areaColor: Color,
    val formatValue: (Float?) -> String,
    val valueRange: ChartValueRange
)

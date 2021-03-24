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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.WeatherAppTheme
import com.example.androiddevchallenge.ui.theme.typography

private enum class TabSlots {
    Tabs,
    Indicator
}

data class TabPosition(
    val left: Dp,
    val width: Dp
) {
    val right = left + width
}

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    tabs: @Composable () -> Unit
) {
    SubcomposeLayout(modifier.selectableGroup()) { constraints ->
        val tabRowWidth = constraints.maxWidth
        val tabMeasurables = subcompose(TabSlots.Tabs, tabs)
        val tabCount = tabMeasurables.size
        val tabWidth = (tabRowWidth / tabCount)
        val tabPlaceables = tabMeasurables.map {
            it.measure(constraints.copy(minWidth = tabWidth, maxWidth = tabWidth))
        }

        val tabRowHeight = tabPlaceables.maxByOrNull { it.height }?.height ?: 0

        val tabPositions = List(tabCount) { index ->
            TabPosition(tabWidth.toDp() * index, tabWidth.toDp())
        }

        layout(tabRowWidth, tabRowHeight) {
            subcompose(TabSlots.Indicator) {
                indicator(tabPositions)
            }.forEach {
                it.measure(Constraints.fixed(tabRowWidth, tabRowHeight)).placeRelative(0, 0)
            }

            tabPlaceables.forEachIndexed { index, placeable ->
                placeable.placeRelative(index * tabWidth, 0)
            }
        }
    }
}

@Composable
fun Tab(
    text: String,
    onClick: () -> Unit,
    isTabSelected: Boolean = false,
) {
    val color by animateColorAsState(
        contentColorFor(if (isTabSelected) colors.primarySurface else colors.background)
    )
    Box(
        modifier = Modifier.selectable(isTabSelected, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, style = typography.body1)
    }
}

@Composable
fun <T : Enum<*>> TabIndicator(
    modifier: Modifier = Modifier,
    tabPositions: List<TabPosition>,
    tab: T
) {
    val transition = updateTransition(
        tab,
        label = "Tab indicator"
    )

    val indicatorLeft by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessMedium) },
        label = "Indicator left"
    ) { page -> tabPositions[page.ordinal].left }

    val indicatorRight by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessMedium) },
        label = "Indicator right"
    ) { page -> tabPositions[page.ordinal].right }

    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.CenterStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .fillMaxSize()
            .background(colors.primary)
    )
}

@Composable
@Preview
fun TabRowWithFilledSelectorPreview2() {

    var selectedTab by remember { mutableStateOf(Tab.TabA) }

    WeatherAppTheme {
        Surface(color = colors.background) {
            TabRow(
                modifier = Modifier
                    .padding(48.dp)
                    .height(35.dp)
                    .border(1.dp, colors.onBackground),
                indicator = { tabPositions ->
                    TabIndicator(Modifier.clip(RoundedCornerShape(25f)), tabPositions, selectedTab)
                },
                tabs = {
                    Tab(
                        text = Tab.TabA.name,
                        onClick = { selectedTab = Tab.TabA },
                        isTabSelected = selectedTab == Tab.TabA
                    )
                    Tab(
                        text = Tab.TabB.name,
                        onClick = { selectedTab = Tab.TabB },
                        isTabSelected = selectedTab == Tab.TabB
                    )
                }
            )
        }
    }
}

private enum class Tab {
    TabA, TabB
}

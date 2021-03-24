# Weather app

<!--- Replace <OWNER> with your Github Username and <REPOSITORY> with the name of your repository. -->
<!--- You can find both of these in the url bar when you open your repository in github. -->
![Workflow result](https://github.com/Xacalet/adc_w4_weather_app/workflows/Check/badge.svg)

## :scroll: Description

An forecast weather app for Android fully written in Compose.

## :bulb: Motivation and Context

Besides some basics on Compose development, like layouts and theming, this project explores some
interesting features related to animations and graphics:

### Animated curvy chart ###

A chart drawn with Bezier curves using Canvas. When a new dataset is provided to the composable
chart, data points are animated from 0-value to their actual values. Also the labels attached to
these values are also animated.

### Tab row with rear indicator ###

A TabRow, entirely based on the existing TabRow from Material, with the difference that this one
draws the indicator behind the tab slots. This allow to use an animated solid box as the indicator
for the selected tab.

### Flipping composables ###

A bean/pill/card that flips when data is changed by animating the RotationY component. To compensate
the mirror effect when the composable is rotated between 90º and 270º, an similar version of the
same composable is displayed instead, adding 180º to the current rotation value. In addition to this
flipping behavior, updated data is only visible after the composable has been flipped. In order to
avoid that the currently visible face gets updated before flipping the composable, each of the two
sides of the composable hold their own mutable state, which only gets updated with the provided data
when the associated side is hidden. Doing so, when the composable is flipped, the previously hidden
side will reveal the update data.

This behavior is implemented by having two versions of the composable to be flipped, which
correspond to the both sides of a plain view: One will be visible a previous version of the provided
data, and updating with fresh data only the hidden face of the composable.

## :camera_flash: Screenshots

<!-- You can add more screenshots here if you like -->
<img src="/results/screenshot_1.png" width="260">
&emsp;<img src="/results/screenshot_2.png" width="260">

## License

```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

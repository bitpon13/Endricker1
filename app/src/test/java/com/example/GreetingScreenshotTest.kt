package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        androidx.compose.foundation.layout.Box(
          modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BrazilGreen),
          contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
          androidx.compose.material3.Text(
            text = "⚽ ENDRICK CLICKER ⚽",
            color = com.example.ui.theme.BrazilYellow,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            fontSize = 24.sp
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

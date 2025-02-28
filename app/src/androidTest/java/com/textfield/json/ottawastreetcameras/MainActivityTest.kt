package com.textfield.json.ottawastreetcameras

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.textfield.json.ottawastreetcameras.ui.main.MainScreen
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testMainActivity() {
        rule.setContent {
            AppTheme {
                MainScreen()
            }
        }
        rule.onNodeWithText("StreetCams").assertIsDisplayed()
    }
}


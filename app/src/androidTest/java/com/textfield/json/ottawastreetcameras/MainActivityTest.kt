package com.textfield.json.ottawastreetcameras

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.textfield.json.ottawastreetcameras.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val mActivityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun listGoesOverTheFold() {
        onView(withText("StreetCams")).check(matches(isDisplayed()))
    }

    @Test
    fun listViewLoads() {
        onData(withId(R.id.section_index_listview)).atPosition(0).check(matches(isDisplayed()))
    }

    @Test
    fun sectionIndexVisibleDefault() {
        onView(withId(R.id.sectionIndex)).check(matches(isDisplayed()))
    }

    @Test
    fun sectionIndexInvisibleAfterSortByDistance() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText(R.string.sort_by_distance)).perform(click())
    }
}


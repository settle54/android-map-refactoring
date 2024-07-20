package campus.tech.kakao.map

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.intent.Intents
import com.kakao.vectormap.KakaoMapSdk
import org.junit.Before


class MainUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun 장소_목록으로_전환_확인() {
        onView(withId(R.id.search_input)).perform(click())
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()))
    }

    @Test
    fun 맵_움직임_여부() {
        onView(withId(R.id.map_view)).perform(swipeUp()).perform(swipeDown())
            .perform(swipeLeft()).perform(
                swipeRight()
            )
    }

    @Test
    fun 바텀시트_보이는지_확인() {
        // given
        Thread.sleep(5000)
        onView(withId(R.id.search_input)).perform(click())
        onView(withId(R.id.searchInput)).perform(replaceText("카페"))

        //when
        Thread.sleep(1500)
        onView(withId(R.id.placesRView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<PlacesAdapter.ViewHolder>(2, click())
        )

        // then
        Thread.sleep(5000)
        onView(withId(R.id.bottom_sheet_layout))
            .check(matches(isDisplayed()))
    }

}
package campus.tech.kakao.map

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import campus.tech.kakao.map.ui.adapter.PlacesAdapter
import campus.tech.kakao.map.ui.activity.SearchActivity
import org.junit.Assert.*
import org.junit.*

class SearchUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SearchActivity::class.java)

    @Test
    fun 장소_목록_스크롤_여부() {
        onView(withId(R.id.searchInput))
            .perform(replaceText("카페"))

        onView(withId(R.id.placesRView))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(15)
            )
    }

    @Test
    fun 장소목록_종료_확인 () {
        // given
        onView(withId(R.id.searchInput)).perform(replaceText("카페"))

        //when
        Thread.sleep(5000)
        onView(withId(R.id.placesRView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<PlacesAdapter.ViewHolder>(2, click())
        )

        // then
        activityRule.scenario.onActivity { activity ->
            assertTrue(activity.isFinishing)
        }
        Thread.sleep(5000)
    }

    @Test
    fun 검색창_삭제_테스트() {
        onView(withId(R.id.searchInput)).perform(replaceText("약국"))
        onView(withId(R.id.deleteInput)).perform(click())
        onView(withId(R.id.searchInput)).check(matches(withText("")))
    }

}
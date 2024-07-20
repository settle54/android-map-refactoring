package campus.tech.kakao.map

import androidx.test.espresso.IdlingResource

class MyIdlingResource: IdlingResource {
    private var callback: IdlingResource.ResourceCallback? = null
    private var isIdle = false
    override fun getName(): String {
        return "MyIdlingResource"
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?){
        this.callback = callback
    }

    override fun isIdleNow(): Boolean {
        val idleNow = isIdle
        if (idleNow) {
            callback?.onTransitionToIdle()
        }
        return idleNow
    }

    fun setIdleState(isIdleNow: Boolean) {
        isIdle = isIdleNow
        if (isIdleNow) {
            callback?.onTransitionToIdle()
        }
    }

}
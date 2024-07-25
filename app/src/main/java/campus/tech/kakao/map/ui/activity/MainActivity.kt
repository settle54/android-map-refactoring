package campus.tech.kakao.map.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import campus.tech.kakao.map.application.MyApplication
import campus.tech.kakao.map.R
import campus.tech.kakao.map.databinding.ActivityMainBinding
import campus.tech.kakao.map.data.model.Place
import campus.tech.kakao.map.databinding.BottomSheetBinding
import campus.tech.kakao.map.databinding.MapErrorBinding
import campus.tech.kakao.map.ui.viewModel.MapViewModel
import campus.tech.kakao.map.utill.BitmapUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var kakaoMap: KakaoMap
    private var label: Label? = null
    private lateinit var styles: LabelStyles

    private lateinit var bottomSheetBinding: BottomSheetBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val viewModel: MapViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        KakaoMapSdk.init(this, "I'm nativeKey")     // 오류확인

        Log.d("onCreate", "")
        lifecycleScope.launch {
            val lastPos = viewModel.getLastPos()
            drawMap(lastPos)
        }
        setBottomSheet()

        binding.searchInput.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startForResult.launch(intent)
        }

        binding.searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun drawMap(latLng: LatLng?) {
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("KakaoMap", "카카오맵 종료")
            }

            override fun onMapError(e: Exception?) {
                Log.e("KakaoMap", "카카오맵 인증실패", e)
                val errorBinding: MapErrorBinding = MapErrorBinding.inflate(layoutInflater)
                setContentView(errorBinding.root)
                val errorMessage = errorBinding.errorMessage
                errorMessage.text = e?.message
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                Log.d("KakaoMap", "카카오맵 실행")
                kakaoMap = p0
                makeLabelStyle()
            }

            override fun getPosition(): LatLng {
                return latLng ?: return super.getPosition()
            }
        })
    }

    private fun addLabel(place: Place) {
        val latLng = LatLng.from(place.latitude.toDouble(), place.longitude.toDouble())
        moveCamera(latLng)
        label = kakaoMap.labelManager?.layer?.addLabel(
            LabelOptions.from(latLng)
                .setStyles(styles).setTexts(place.name)
        )
    }

    private fun moveCamera(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng, 15)
        Log.d("MainAct State", "Intent is: $intent")
        Log.d("kakaomap", "moveCamera: $kakaoMap")
        kakaoMap?.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
    }

    private fun makeLabelStyle() {
        val marker = BitmapUtils.vectorToBitmap(this, R.drawable.pin_drop_24px)

        styles = LabelStyles.from(
            "myLabel",
            LabelStyle.from(marker)
                .setTextStyles(32, Color.BLACK, 1, Color.GRAY)
                .setZoomLevel(kakaoMap.minZoomLevel)
        )
        styles = kakaoMap.labelManager?.addLabelStyles(styles)!!
    }

    private fun showBottomSheet(place: Place) {
        bottomSheetBinding.data = place
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun setBottomSheet() {
        bottomSheetBinding = DataBindingUtil.bind(binding.includedBottomSheet.root)!!

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.bottomSheetLayout)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.searchWindow.isInvisible = (newState < BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBinding.name.isInvisible = (newState == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBinding.category.isInvisible = (newState == BottomSheetBehavior.STATE_COLLAPSED)
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            override fun onSlide(p0: View, p1: Float) {
                if (p1 > 0.5) {
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
                    }
                } else if (p1 > 0) {
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED;
                    }
                }
            }
        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.halfExpandedRatio = 0.13f
    }

    private val startForResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val intent: Intent = requireNotNull(result.data)
                    val place: Place? =
                        IntentCompat.getParcelableExtra(intent, "place", Place::class.java)
                    place?.let {
                        requireNotNull(kakaoMap.labelManager?.layer).remove(label)
                        addLabel(it)
                        showBottomSheet(it)
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
        Log.d("intent2", "Intent is: $intent")
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }
}

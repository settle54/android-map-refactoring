package campus.tech.kakao.map.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val name: String,
    val address: String,
    val category: String = "",
    val longitude: String = "",
    val latitude: String = ""
): Parcelable

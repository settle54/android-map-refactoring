package campus.tech.kakao.map.model

import java.io.Serializable

data class Place (
    val name: String,
    val address: String,
    val category: String = ""
)
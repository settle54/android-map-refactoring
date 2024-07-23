package campus.tech.kakao.map.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import campus.tech.kakao.map.data.model.DBPlace.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class DBPlace (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "address") val address: String = "",
    @ColumnInfo(name = "category") val category: String = "",
) {

    companion object {
        const val TABLE_NAME = "room_places"
        const val DATABASE_NAME = "room_database.db"
    }
}
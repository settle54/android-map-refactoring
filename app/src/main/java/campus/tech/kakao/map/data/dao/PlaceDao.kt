package campus.tech.kakao.map.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import campus.tech.kakao.map.data.model.DBPlace
import campus.tech.kakao.map.data.model.DBPlace.Companion.TABLE_NAME
import campus.tech.kakao.map.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAllPlaces(): List<DBPlace>

    @Insert
    suspend fun insertAll(vararg dbPlace: DBPlace)

    @Delete
    suspend fun delete(dbPlace: DBPlace)

}
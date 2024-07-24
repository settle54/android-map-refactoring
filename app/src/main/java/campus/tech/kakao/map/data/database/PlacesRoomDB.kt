package campus.tech.kakao.map.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.model.DBPlace
import campus.tech.kakao.map.data.model.DBPlace.Companion.DATABASE_NAME

@Database(entities = [DBPlace::class], version = 1)
abstract class PlacesRoomDB: RoomDatabase() {
    abstract fun placeDao(): PlaceDao


    companion object {
        @Volatile
        private var Instance: PlacesRoomDB? = null

        fun getDatabase(context: Context): PlacesRoomDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PlacesRoomDB::class.java, DATABASE_NAME
                ).build().also { Instance = it }
            }
        }
    }

}
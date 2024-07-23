package campus.tech.kakao.map.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.model.DBPlace
import campus.tech.kakao.map.data.model.DBPlace.Companion.DATABASE_NAME

@Database(entities = [DBPlace::class], version = 1)
abstract class PlacesDatabase: RoomDatabase() {
    abstract fun placeDao(): PlaceDao


    companion object {
        @Volatile
        private var Instance: PlacesDatabase? = null

        fun getDatabase(context: Context): PlacesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PlacesDatabase::class.java, DATABASE_NAME
                ).build().also { Instance = it }
            }
        }
    }

}
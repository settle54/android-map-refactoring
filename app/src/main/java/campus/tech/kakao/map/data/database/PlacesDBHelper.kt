package campus.tech.kakao.map.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import campus.tech.kakao.map.data.model.Place
import dagger.hilt.android.qualifiers.ApplicationContext
import java.sql.SQLException
import javax.inject.Inject

class PlacesDBHelper @Inject constructor(@ApplicationContext context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_TABLE)
        } catch (e: SQLException) {

        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun checkPlaceExist(db: SQLiteDatabase,place: Place): Boolean {
        val projection = arrayOf(NAME)
        val selection = "address = ?"
        val selectionArgs = arrayOf(place.address)
        val cursor = db.query(
            TABLE_NAME, projection, selection, selectionArgs,
            null, null, null
        )
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun insertPlace(place: Place) {
        val db = this.writableDatabase
        if (checkPlaceExist(db, place)) {
            return
        }
        val values = ContentValues().apply {
            put(NAME, place.name)
            put(ADDRESS, place.address)
            put(CATEGORY, place.category)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertPlaces(places: List<Place>) {
        val db = this.writableDatabase
        for (place in places) {
            if (checkPlaceExist(db, place)) {
                continue
            }
            val values = ContentValues().apply {
                put(NAME, place.name)
                put(ADDRESS, place.address)
                put(CATEGORY, place.category)
            }
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }


    @SuppressLint("Range")
    fun getAllPlaces(): List<Place> {
        val rdb = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = rdb.rawQuery(query, null)
        val places = mutableListOf<Place>()
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val address = cursor.getString(cursor.getColumnIndex(ADDRESS))
                val category = cursor.getString(cursor.getColumnIndex(CATEGORY))

                val place = Place(name, address, category)
                places.add(place)
            } while (cursor.moveToNext())
        }
        cursor.close()
        rdb.close()
        return places
    }

    fun deletePlace(place: Place) {
        val db = this.writableDatabase
        db.delete("$TABLE_NAME", "$NAME=?", arrayOf(place.name))
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "places.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "places_table"
        const val NAME = "NAME"
        const val ADDRESS = "ADDRESS"
        const val CATEGORY = "CATEGORY"

        private const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$NAME TEXT, $ADDRESS TEXT, $CATEGORY TEXT);"
    }
}
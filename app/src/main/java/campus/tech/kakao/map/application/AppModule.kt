package campus.tech.kakao.map.application

import android.content.Context
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.database.PlacesDBHelper
import campus.tech.kakao.map.data.database.PlacesRoomDB
import campus.tech.kakao.map.data.repository.MapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMapRepository(
        @ApplicationContext context: Context,
        placeDao: PlaceDao,
        placesDBHelper: PlacesDBHelper,
        placesRoomDB: PlacesRoomDB
    ): MapRepository {
        return MapRepository(context, placeDao, placesDBHelper, placesRoomDB)
    }

    @Provides
    @Singleton
    fun providePlaceDao(placesRoomDB: PlacesRoomDB): PlaceDao {
        return placesRoomDB.placeDao()
    }

    @Provides
    @Singleton
    fun provideDBHelper(@ApplicationContext context: Context): PlacesDBHelper {
        return PlacesDBHelper(context)
    }

    @Provides
    @Singleton
    fun providePlacesDatabase (@ApplicationContext context: Context): PlacesRoomDB {
        return PlacesRoomDB.getDatabase(context)
    }

}
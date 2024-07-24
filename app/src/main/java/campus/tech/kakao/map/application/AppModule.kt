package campus.tech.kakao.map.application

import android.content.Context
import campus.tech.kakao.map.data.dao.PlaceDao
import campus.tech.kakao.map.data.database.PlacesDatabase
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
    fun provideMapRepository(@ApplicationContext context : Context): MapRepository {
        val placeDao: PlaceDao = PlacesDatabase.getDatabase(context).placeDao()
        return MapRepository(context, placeDao)
    }

}
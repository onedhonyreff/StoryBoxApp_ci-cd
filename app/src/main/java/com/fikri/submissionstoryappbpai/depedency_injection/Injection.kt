package com.fikri.submissionstoryappbpai.depedency_injection

import android.content.Context
import android.location.Geocoder
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.*
import java.util.*

object Injection {
    fun provideStoryListRepository(context: Context): StoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(pref, database, apiService)
    }

    fun provideStoryMapsRepository(context: Context): MapsStoryRepository {
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val geocoder = Geocoder(context, Locale.getDefault())
        val apiService = ApiConfig.getApiService()
        return MapsStoryRepository(resources, pref, geocoder, apiService)
    }

    fun provideCreateStoryRepository(context: Context): CreateStoryRepository {
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return CreateStoryRepository(resources, pref, apiService)
    }

    fun provideCreateMapStoryRepository(context: Context): CreateStoryMapRepository {
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val geocoder = Geocoder(context, Locale.getDefault())
        val apiService = ApiConfig.getApiService()
        return CreateStoryMapRepository(resources, pref, geocoder, apiService)
    }

    fun provideMainActivityRepository(context: Context): MainActivityRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return MainActivityRepository(pref)
    }

    fun provideLoginRepository(context: Context): LoginRepository {
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return LoginRepository(resources, pref, apiService)
    }

    fun provideRegisterRepository(context: Context): RegisterRepository {
        val resources = context.resources
        val apiService = ApiConfig.getApiService()
        return RegisterRepository(resources, apiService)
    }

    fun provideHomeBottomNavRepository(context: Context): HomeBottomNavRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val remoteKeysDao = database.remoteKeysDao()
        val storyDao = database.storyDao()
        return HomeBottomNavRepository(pref, remoteKeysDao, storyDao)
    }

    fun provideDisplayConfigurationRepository(context: Context): DisplayConfigurationRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return DisplayConfigurationRepository(pref)
    }

    fun provideMoreMenuRepository(context: Context): MoreMenuRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return MoreMenuRepository(pref)
    }
}
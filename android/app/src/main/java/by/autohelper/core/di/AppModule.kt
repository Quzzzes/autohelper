package by.autohelper.core.di

import by.autohelper.core.network.ApiClient
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(apiClient: ApiClient): ApiService =
        apiClient.retrofit.create(ApiService::class.java)
}

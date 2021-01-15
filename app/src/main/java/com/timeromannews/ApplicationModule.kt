package com.timeromannews

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.timeromannews.di.ApplicationContext
import com.timeromannews.network.NetworkService
import com.timeromannews.util.Constant
import com.timeromannews.util.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApplicationModule constructor(baseApplication: BaseApplication){
    var base : BaseApplication = baseApplication

    private val _app: Application = baseApplication

    @Provides
    internal fun providesApplication(): Application {
        return _app
    }

    @Provides
    @ApplicationContext
    internal fun providesContext(): Context {
        return _app
    }

    @Provides
    @Singleton
    fun provideOKHttpClient() : OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG){
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HeaderInterceptor())
            .readTimeout(1200, TimeUnit.SECONDS)
            .connectTimeout(1200,TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    internal open fun providesGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal open fun provideInterfaceServiceApi(httpClient: OkHttpClient, gson: Gson): NetworkService {
        return NetworkService.Creator().apiService(Constant.URL.BASE_URL,httpClient, gson)
    }
}
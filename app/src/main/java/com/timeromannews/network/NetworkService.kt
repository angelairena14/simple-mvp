package com.timeromannews.network

import com.google.gson.Gson
import com.timeromannews.model.LoginResponse
import com.timeromannews.model.ProfileResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import javax.inject.Inject

interface NetworkService {
    @POST("auth/login")
    fun postLogin(@Body body: HashMap<String, Any?>): Observable<LoginResponse>

    @GET("me/profile")
    fun fetchProfile(): Observable<ProfileResponse>

    class Creator {
        @Inject
        fun apiService(url: String, httpClient: OkHttpClient, gson: Gson): NetworkService {
            val retrofit = Retrofit
                .Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()

            return retrofit.create(NetworkService::class.java)
        }
    }
}
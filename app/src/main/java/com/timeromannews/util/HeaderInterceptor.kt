package com.timeromannews.util

import android.content.Context
import android.util.Log
import de.adorsys.android.securestoragelibrary.SecurePreferences
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class HeaderInterceptor (var context: Context?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var token = if (SecurePreferences.contains("token")) SecurePreferences.getStringValue("token","") else ""
        var request = chain.request()
        var requestBuilder = request.newBuilder()
            .addHeader("Authorization","Bearer $token")
        request = requestBuilder.build()

        var response= chain.proceed(request)
        var stringData = ""
        if (response.code == 200 || response.code == 201){
            response.body?.string()?.let { json ->
                stringData = json
            }
        } else {
//            RxBus.publish(ApiBus(response.code))
        }
        val contentType = response.body?.contentType()
        val body = ResponseBody.create(contentType,stringData)
        return response.newBuilder().body(body).build()
    }
}
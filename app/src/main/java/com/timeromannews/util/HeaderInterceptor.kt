package com.timeromannews.util

import android.util.Log
import com.google.gson.Gson
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_200
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_201
import com.timeromannews.util.Constant.RETROFIT_ERROR.ERROR
import com.timeromannews.util.Constant.RETROFIT_ERROR.FIELDS
import com.timeromannews.util.Constant.RETROFIT_ERROR.MESSAGE
import com.timeromannews.util.Constant.RETROFIT_ERROR.NAME
import de.adorsys.android.securestoragelibrary.SecurePreferences
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var token = if (isTokenExpired()) getNewToken() else {
            if (SecurePreferences.contains("token")) SecurePreferences.getStringValue(
                "token",
                ""
            ) else ""
        }
        var request = chain.request()
        var requestBuilder = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
        request = requestBuilder.build()
        var response = chain.proceed(request)
        var stringData = ""
        response.body?.string()?.let { json ->
            stringData = json
            try {
                val obj = JSONObject(json)
                if (response.code != RESPONSE_200 || response.code != RESPONSE_201) {
                    if (obj.has(FIELDS)) {
                        var errorJson = obj.getJSONArray(FIELDS)
                        for (i in 0 until errorJson.length()) {
                            RxBus.publish(
                                RetrofitResponse(
                                    response.code,
                                    obj.getString(MESSAGE),
                                    errorJson.getJSONObject(i).getString(ERROR),
                                    errorJson.getJSONObject(i).getString(NAME)
                                )
                            )
                        }
                    } else {
                        RxBus.publish(
                            RetrofitResponse(
                                response.code,
                                obj.getString(MESSAGE),
                                "",
                                ""
                            )
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val contentType = response.body?.contentType()
        val body = ResponseBody.create(contentType, stringData)
        return response.newBuilder().body(body).build()
    }

    private fun getNewToken(): String {
        if (!SecurePreferences.contains("token")){
            return ""
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val map = HashMap<String, String>()
        map["username"] = SecurePreferences.getStringValue("username","")?:""
        map["password"] = SecurePreferences.getStringValue("password","")?:""
        val reqbody = RequestBody.create(null, Gson().toJson(map))
        val req = Request.Builder()
            .url(Constant.URL.BASE_URL+ "auth/login")
            .method("POST", reqbody).build()
        val response = client.newCall(req).execute()
        var newToken = ""
        if (response.code == RESPONSE_200 || response.code == RESPONSE_201) {
            response.body?.apply {
                val jsonBody = this.string()
                val jsonObj = JSONObject(jsonBody)
                if (jsonObj.has("token")) {
                    val token = jsonObj.getString("token")
                    val expiredAt = jsonObj.getString("expires_at")
                    SecurePreferences.setValue("expired_at",expiredAt)
                    newToken = token
                    SecurePreferences.setValue("token",token)
                }
            } ?: kotlin.run {
            }
        } else {
            RxBus.publish(
                RetrofitResponse(
                    response.code,
                    "",
                    "",
                    ""
                )
            )
        }
        return newToken
    }

    private fun isTokenExpired() : Boolean{
        return try {
            val sdformat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val expiredDate = sdformat.parse(SecurePreferences.getStringValue("expired_at","")
                ?.replace("T"," ")
                ?.replace("Z","")
                ?:"2020-07-16T11:30:00Z")
            var calendar = Calendar.getInstance()
            calendar.time = expiredDate
            /*
            * currently adding 7 because from API the expired_at field return 4 hours earlier
            * than current time e.g : logged in at 10:50 then expired_at return 06:50
            * so i add GMT+7 from expired_at as default.
            * in this case 06:50 plus GMT+7 = 12.50 (expired time)
            * */
            calendar.add(Calendar.HOUR,7)
            val now = sdformat.parse(
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )
            //handling auto refresh token 1 hour before expired_time GMT+7 exceed
            calendar.add(Calendar.HOUR,-1)
            now >= calendar.time
        } catch (e : Exception) {
            true
        }
    }
}

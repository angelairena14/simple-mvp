package com.timeromannews.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timeromannews.R
import com.timeromannews.base.BaseActivity
import com.timeromannews.model.ProfileResponse
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.util.CacheManager
import com.timeromannews.util.Constant
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_401
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_news.*
import java.lang.Exception
import javax.inject.Inject


class NewsActivity : BaseActivity(), NewsContract.View{
    @Inject
    lateinit var newsPresenter: NewsPresenter
    private lateinit var cacheManager: CacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        getActivityComponent()?.inject(this)
        cacheManager = CacheManager(this)
        newsPresenter.attachView(this)
        newsPresenter.getProfile()
        newsPresenter.listenError()
//        if (isTokenExpired()) showRedirectToLoginDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::newsPresenter.isInitialized) newsPresenter.dispose()
    }

    override fun initProfileFromCache() {
        val type = object : TypeToken<ProfileResponse>() {}.type
        try {
            val profile = cacheManager.readJson(type, Constant.CACHE_FILENAME.PROFILE) as ProfileResponse
            if (profile != null) onSuccessGetProfile(profile)
            else setError(getString(R.string.failed_to_load_data))
        } catch (e : Exception){
            setError(getString(R.string.failed_to_load_data))
        }
    }

    override fun onSuccessGetProfile(response: ProfileResponse) {
        tv_name.text = response.name
        tv_bio.text = response.bio
        tv_web.text = response.web
        Glide.with(this)
            .load(response.picture)
            .apply(RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(iv_profile)
        val type = object : TypeToken<ProfileResponse>() {}.type
        cacheManager.writeJson(response,type,Constant.CACHE_FILENAME.PROFILE)
    }

    override fun setError(message: String) {
        showToastMessage(message)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }
    override fun onServiceError(retrofitResponse: RetrofitResponse) {
        if (retrofitResponse.code == RESPONSE_401) showRedirectToLoginDialog()
    }

    fun showRedirectToLoginDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.your_token_is_expired))
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                SecurePreferences.removeValue("token")
                redirectToLogin(this)
            }
        val alert = builder.create()
        alert.show()
    }
}

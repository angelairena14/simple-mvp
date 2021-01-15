package com.timeromannews.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.gson.reflect.TypeToken
import com.timeromannews.R
import com.timeromannews.base.BaseActivity
import com.timeromannews.model.ProfileResponse
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.model.newsresponse.News
import com.timeromannews.model.newsresponse.NewsResponse
import com.timeromannews.ui.adapter.NewsListAdapter
import com.timeromannews.util.CacheManager
import com.timeromannews.util.Constant
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_401
import com.timeromannews.util.testing.AppSchedulerProvider
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


class HomeActivity : BaseActivity(), HomeContract.View{
    @Inject
    lateinit var homePresenter: HomePresenter
    private lateinit var cacheManager: CacheManager
    private var adapter = NewsListAdapter()
    private var skeletonScreen: RecyclerViewSkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getActivityComponent()?.inject(this)
        cacheManager = CacheManager(this)
        homePresenter.setScheduleProvider(AppSchedulerProvider())
        homePresenter.attachView(this)
        homePresenter.getProfile()
        homePresenter.getNews()
        homePresenter.listenError()
        initSkeleton()
        viewListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::homePresenter.isInitialized) homePresenter.dispose()
    }

    override fun onSuccessGetProfile(response: ProfileResponse) {
        tv_name.text = response.name
        tv_bio.text = response.bio
        tv_web.text = response.web
        Glide.with(this)
            .load(response.picture)
            .placeholder(ContextCompat.getDrawable(this,R.drawable.img_not_available))
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
        skeletonScreen?.show()
    }

    override fun hideLoading() {
        skeletonScreen?.hide()
    }
    override fun onServiceError(retrofitResponse: RetrofitResponse) {
        if (retrofitResponse.code == RESPONSE_401) showRedirectToLoginDialog()
    }
    override fun onSuccessGetNews(response: NewsResponse) {
        bindNewsToList(response.data)
        val type = object : TypeToken<NewsResponse>() {}.type
        cacheManager.writeJson(response,type,Constant.CACHE_FILENAME.NEWS)
    }

    override fun initNewsFromCache() {
        val type = object : TypeToken<NewsResponse>() {}.type
        try {
            val news = cacheManager.readJson(type, Constant.CACHE_FILENAME.NEWS) as NewsResponse
            if (news != null) bindNewsToList(news.data)
            else loadListErrorLayout()
        } catch (e : Exception){
            loadListErrorLayout()
        }
    }

    override fun initProfileFromCache() {
        val type = object : TypeToken<ProfileResponse>() {}.type
        try {
            val profile = cacheManager.readJson(type, Constant.CACHE_FILENAME.PROFILE) as ProfileResponse
            if (profile != null) onSuccessGetProfile(profile)
            else setError(getString(R.string.failed_to_load_data))
        } catch (e : Exception){
            loadProfileErrorLayout()
            setError(getString(R.string.failed_to_load_data))
        }
    }

    private fun viewListener(){
        swipe_refresh_home.setOnRefreshListener {
            swipe_refresh_home.isRefreshing = false
            homePresenter.getProfile()
            homePresenter.getNews()
        }
    }

    private fun bindNewsToList(data : ArrayList<News>){
        layout_news_list.visibility = View.VISIBLE
        label_hot_topics.visibility = View.VISIBLE
        label_hot_topics_captions.visibility = View.VISIBLE
        layout_news_list_failed.visibility = View.GONE
        adapter.setNews(data)
        adapter.onItemClicked = { openChromeBrowser(it) }
        rv_news.adapter = adapter
    }

    private fun loadListErrorLayout(){
        layout_news_list.visibility = View.GONE
        label_hot_topics.visibility = View.GONE
        label_hot_topics_captions.visibility = View.GONE
        layout_news_list_failed.visibility = View.VISIBLE
    }

    private fun loadProfileErrorLayout(){
        tv_name.text = "-"
        tv_bio.text = "-"
        tv_web.text = "-"
        Glide.with(this)
            .load(ContextCompat.getDrawable(this,R.drawable.img_not_available))
            .apply(RequestOptions.circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(iv_profile)
    }

    private fun showRedirectToLoginDialog(){
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

    private fun initSkeleton(){
        skeletonScreen = Skeleton.bind(rv_news)
            .adapter(adapter)
            .shimmer(true)
            .frozen(false)
            .color(R.color.whitePrimary)
            .duration(1500)
            .count(10)
            .load(R.layout.item_news_skeleton)
            .show()
    }

    private fun openChromeBrowser(url : String){
        var builder = CustomTabsIntent.Builder()
        var customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}

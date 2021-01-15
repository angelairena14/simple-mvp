package com.timeromannews.ui

import com.timeromannews.base.BaseInterface
import com.timeromannews.model.ProfileResponse
import com.timeromannews.model.newsresponse.NewsResponse

interface HomeContract {
    interface View : BaseInterface {
        fun onSuccessGetProfile(response: ProfileResponse)
        fun onSuccessGetNews(response : NewsResponse)
        fun initProfileFromCache()
        fun initNewsFromCache()
    }

    interface Presenter {
        fun getProfile()
        fun getNews()
    }
}
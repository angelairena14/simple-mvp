package com.timeromannews.ui

import com.timeromannews.base.BaseInterface
import com.timeromannews.model.ProfileResponse

interface NewsContract {
    interface View : BaseInterface {
        fun onSuccessGetProfile(response: ProfileResponse)
        fun initProfileFromCache()
    }

    interface Presenter {
        fun getProfile()
    }
}
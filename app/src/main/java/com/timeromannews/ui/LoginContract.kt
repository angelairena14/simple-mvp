package com.timeromannews.ui

import com.timeromannews.base.BaseInterface
import com.timeromannews.model.LoginResponse

interface LoginContract {
    interface View : BaseInterface {
        fun onSuccessLogin(response: LoginResponse)
    }

    interface Presenter {
        fun login(email: String, password: String)
        fun dispose()
    }
}
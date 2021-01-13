package com.timeromannews.ui

import android.util.Log
import com.google.gson.Gson
import com.timeromannews.base.BasePresenter
import com.timeromannews.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class LoginPresenter @Inject constructor(val api: NetworkService) :
    BasePresenter<LoginContract.View>(),
    LoginContract.Presenter {

    private var disposeLogin: Disposable = Disposables.empty()

    override fun dispose() {
        if (!disposeLogin.isDisposed) disposeLogin.dispose()
    }
    override fun login(email: String, password: String) {
        var map = HashMap<String,Any?>()
        map["username"] = email
        map["password"] = password
        disposeLogin =  api.postLogin(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mvpView?.showLoading() }
            .doAfterTerminate { mvpView?.hideLoading() }
            .subscribe({
                mvpView?.onSuccessLogin(it)
            },{
                if (it is HttpException) {
                    if (it.code() == 401){
//                        invalid credential
                    } else {
                        Log.i("error_is",it.code().toString())
                    }
                }
            })
    }
}
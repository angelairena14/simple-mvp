package com.timeromannews.ui

import com.timeromannews.base.BasePresenter
import com.timeromannews.base.BaseServiceInterface
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.network.NetworkService
import com.timeromannews.util.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val api: NetworkService) :
    BasePresenter<LoginContract.View>(),
    LoginContract.Presenter,
    BaseServiceInterface {
    private var disposeLogin: Disposable = Disposables.empty()
    private var disposablesError: Disposable = Disposables.empty()

    override fun dispose() {
        if (!disposeLogin.isDisposed) disposeLogin.dispose()
        if (!disposablesError.isDisposed) disposablesError.dispose()
    }

    override fun login(email: String, password: String) {
        var map = HashMap<String, Any?>()
        map["username"] = email
        map["password"] = password
        disposeLogin = api.postLogin(map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mvpView?.showLoading() }
            .doAfterTerminate { mvpView?.hideLoading() }
            .subscribe({
                mvpView?.onSuccessLogin(it)
            }, {})
    }

    override fun listenError() {
        disposablesError = RxBus.listen(RetrofitResponse::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                mvpView?.onServiceError(it)
            }, {})
    }
}
package com.timeromannews.ui

import com.timeromannews.base.BasePresenter
import com.timeromannews.base.BaseServiceInterface
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.network.NetworkService
import com.timeromannews.util.RxBus
import com.timeromannews.util.testing.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val api: NetworkService) :
    BasePresenter<LoginContract.View>(),
    LoginContract.Presenter,
    BaseServiceInterface {

    lateinit var scheduler : SchedulerProvider
    private var disposeLogin: Disposable = Disposables.empty()
    private var disposablesError: Disposable = Disposables.empty()

    override fun dispose() {
        if (!disposeLogin.isDisposed) disposeLogin.dispose()
        if (!disposablesError.isDisposed) disposablesError.dispose()
    }

    override fun setScheduleProvider(scheduler: SchedulerProvider) {
        this.scheduler = scheduler
    }

    override fun login(email: String, password: String) {
        var map = HashMap<String, Any?>()
        map["username"] = email
        map["password"] = password
        disposeLogin = api.postLogin(map)
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .doOnSubscribe { mvpView?.showLoading() }
            .doAfterTerminate { mvpView?.hideLoading() }
            .subscribe({
                mvpView?.onSuccessLogin(it)
            }, {})
    }

    override fun listenError() {
        disposablesError = RxBus.listen(RetrofitResponse::class.java)
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe({
                mvpView?.onServiceError(it)
            }, {})
    }
}
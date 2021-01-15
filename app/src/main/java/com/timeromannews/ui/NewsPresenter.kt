package com.timeromannews.ui

import android.util.Log
import retrofit2.HttpException
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

class NewsPresenter @Inject constructor(val api: NetworkService) :
    BasePresenter<NewsContract.View>(),
    NewsContract.Presenter,
    BaseServiceInterface {
    private var disposeGetProfile: Disposable = Disposables.empty()
    private var disposablesError: Disposable = Disposables.empty()

    override fun dispose() {
        if (!disposeGetProfile.isDisposed) disposeGetProfile.dispose()
        if (!disposablesError.isDisposed) disposablesError.dispose()
    }

    override fun getProfile() {
        disposeGetProfile = api.fetchProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mvpView?.showLoading() }
            .doAfterTerminate { mvpView?.hideLoading() }
            .subscribe({
                mvpView?.onSuccessGetProfile(it)
            }, {
                if (it is HttpException) {
                    mvpView?.initProfileFromCache()
                }
            })
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
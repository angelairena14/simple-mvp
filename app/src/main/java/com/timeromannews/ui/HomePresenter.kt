package com.timeromannews.ui

import retrofit2.HttpException
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

class HomePresenter @Inject constructor(val api: NetworkService) :
    BasePresenter<HomeContract.View>(),
    HomeContract.Presenter,
    BaseServiceInterface {

    lateinit var scheduler : SchedulerProvider
    private var disposeGetProfile: Disposable = Disposables.empty()
    private var disposeGetNews: Disposable = Disposables.empty()
    private var disposablesError: Disposable = Disposables.empty()

    override fun dispose() {
        if (!disposeGetProfile.isDisposed) disposeGetProfile.dispose()
        if (!disposablesError.isDisposed) disposablesError.dispose()
        if (!disposeGetNews.isDisposed) disposeGetNews.dispose()
    }

    override fun setScheduleProvider(scheduler: SchedulerProvider) {
        this.scheduler = scheduler
    }

    override fun getProfile() {
        disposeGetProfile = api.fetchProfile()
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
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
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribe({
                mvpView?.onServiceError(it)
            }, {})
    }

    override fun getNews() {
        disposeGetNews = api.fetchNews()
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .doOnSubscribe { mvpView?.showLoading() }
            .doAfterTerminate { mvpView?.hideLoading() }
            .subscribe({
                mvpView?.onSuccessGetNews(it)
            }, {
                if (it is HttpException) {
                    mvpView?.initNewsFromCache()
                }
            })
    }
}
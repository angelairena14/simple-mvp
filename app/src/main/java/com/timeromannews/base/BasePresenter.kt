package com.timeromannews.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T : BaseInterface> : Presenter<T> {
    var mvpView: T? = null
    var disposables: CompositeDisposable? = null

    override fun attachView(baseInterface: T) {
        this.mvpView = baseInterface
        disposables = CompositeDisposable()
    }

    override fun detachView() {
        mvpView = null
        disposables?.dispose()
        disposables?.clear()
    }
}
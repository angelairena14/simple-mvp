package com.timeromannews.base

interface Presenter<V : BaseInterface> {
    fun attachView(baseInterface: V)
    fun detachView()
}
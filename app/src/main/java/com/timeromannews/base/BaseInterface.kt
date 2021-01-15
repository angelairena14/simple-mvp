package com.timeromannews.base

import com.timeromannews.model.RetrofitResponse

interface BaseInterface{
    fun setError(message: String)
    fun showLoading()
    fun hideLoading()
    fun onServiceError(retrofitResponse: RetrofitResponse)
}
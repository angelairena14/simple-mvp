package com.timeromannews.base

import com.timeromannews.util.testing.SchedulerProvider

interface BaseServiceInterface {
    fun dispose()
    fun listenError()
    fun setScheduleProvider(scheduler : SchedulerProvider)
}
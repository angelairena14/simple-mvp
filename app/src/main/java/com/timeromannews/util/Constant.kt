package com.timeromannews.util

object Constant {
    object URL {
        const val BASE_URL = "http://34.121.153.157/"
    }

    object CACHE_FILENAME {
        const val PROFILE = "profile.json"
    }

    object RETROFIT_ERROR {
        const val FIELDS = "fields"
        const val NAME = "name"
        const val ERROR = "error"
        const val MESSAGE = "message"
    }

    object RETROFIT_ERROR_TYPE {
        const val VALIDATION_FAILED = "validation-failed"
    }

    object HTTP_RESPONSE_CODE {
        const val RESPONSE_200 = 200
        const val RESPONSE_201 = 201
        const val RESPONSE_401 = 401
        const val RESPONSE_422 = 422
        const val RESPONSE_500 = 500
    }
}
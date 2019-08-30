package com.library.android.appwall.library.dependencies

import com.github.kittinunf.fuel.core.ResponseHandler
import com.github.kittinunf.fuel.core.ResponseResultHandler
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.library.android.appwall.library.AppWall
import com.library.android.appwall.library.model.App

class ApiDependencies {

    fun fetchData(responseHandler: ResponseResultHandler<List<App>>) = AppWall.getUrl().httpGet().responseObject(responseHandler)
}
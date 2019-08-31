package com.library.android.appwall.library.dependencies

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Handler
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.library.android.appwall.library.AppWall
import com.library.android.appwall.library.model.App

class ApiDependencies {

    fun fetchData(responseHandler: (Request, Response, Result<List<App>, FuelError>) -> Unit) =
        AppWall.getUrl().httpGet().responseObject(responseHandler)

}
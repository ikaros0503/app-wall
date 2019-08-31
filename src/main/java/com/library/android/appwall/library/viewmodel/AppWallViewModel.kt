package com.library.android.appwall.library.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.kittinunf.fuel.core.Handler
import com.library.android.appwall.library.AppWall
import com.library.android.appwall.library.coroutines.viewModelScope
import com.library.android.appwall.library.dependencies.ApiDependencies
import com.library.android.appwall.library.dependencies.LocalAppRepository
import com.library.android.appwall.library.model.App
import kotlinx.coroutines.launch

class AppWallViewModel(apiDependencies: ApiDependencies, localAppRepository: LocalAppRepository) :
    ExtendedViewModel() {

    private val listApps = MutableLiveData<List<App>>().apply { value = listOf() }

    fun getListApps(): LiveData<List<App>> = listApps

    init {
        apiDependencies.fetchData { _, _, result ->
            result.fold({ results ->
                viewModelScope.launch {
                    listApps.value =
                        results.filter {
                            it.packageId != AppWall.getPackageId()
                                    && !localAppRepository.isAppInstall(it.packageId)
                        }.sortedByDescending { it.isNew }
                }
            }) {
                it.printStackTrace()
            }
        }
    }

    fun isReadyForUse() = (listApps.value?.size ?: 0) > 0

    class Factory(
        private val apiDependencies: ApiDependencies,
        private val localAppRepository: LocalAppRepository
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AppWallViewModel(apiDependencies, localAppRepository) as T
        }
    }

}
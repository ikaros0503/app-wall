package com.library.android.appwall.library.viewmodel

import androidx.lifecycle.*
import com.library.android.appwall.library.AppWall
import com.library.android.appwall.library.dependencies.ApiDependencies
import com.library.android.appwall.library.dependencies.LocalAppRepository
import com.library.android.appwall.library.model.App
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppWallViewModel(apiDependencies: ApiDependencies, localAppRepository: LocalAppRepository) :
    ViewModel() {

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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

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
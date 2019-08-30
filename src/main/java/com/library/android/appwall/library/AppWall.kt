package com.library.android.appwall.library

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.library.android.appwall.library.dependencies.ApiDependencies
import com.library.android.appwall.library.dependencies.LocalAppRepository
import com.library.android.appwall.library.viewmodel.AppWallViewModel

object AppWall {
    private var setLock = Any()
    private var url: String = ""
    private var packageId: String = ""
    private var viewModelStore = ViewModelStore()
    private lateinit var viewModel: AppWallViewModel

    fun initialize(url: String, context: Context) {
        synchronized(setLock) {
            if (this.url.isEmpty()) {
                this.url = url
            }
            if (this.packageId.isEmpty()) {
                this.packageId = context.packageName
            }
        }
        initViewModel(context)
    }

    internal fun getViewModelStore() = viewModelStore

    private fun initViewModel(context: Context) {
        synchronized(setLock) {
            viewModel = ViewModelProvider(
                viewModelStore,
                AppWallViewModel.Factory(ApiDependencies(), LocalAppRepository(context))
            ).get(AppWallViewModel::class.java)
        }
    }

    fun getNumberOfNewItem(): Int {
        return viewModel.getListApps().value?.count { it.isNew } ?: 0
    }

    fun observerWhenReadyForUse(owner: LifecycleOwner, action: (Boolean) -> Unit) {
        viewModel.getListApps().observe(owner, Observer {
            action.invoke((it ?: listOf()).isNotEmpty())
        })
    }

    fun isReadyForUse() = synchronized(setLock) {
        viewModel.isReadyForUse()
    }

    fun getUrl() = synchronized(setLock) {
        url
    }

    fun getPackageId() = synchronized(setLock) {
        packageId
    }
}

fun Context.goStore(packageId: String) {
    try {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageId")).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
    } catch (e: Exception) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageId")
            ).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
    }
}


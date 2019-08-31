package com.library.android.appwall.library.dependencies

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class LocalAppRepository(private val context: Context) {

    private val installedApps: List<ApplicationInfo> = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    fun isAppInstall(packageId: String): Boolean {
        return installedApps.firstOrNull { it.packageName == packageId } != null
    }
}
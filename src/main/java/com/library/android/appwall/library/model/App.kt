package com.library.android.appwall.library.model

import java.util.*

data class App(
    val packageId: String,
    val stars: Float,
    val icon: String,
    val isNew: Boolean = true,
    val title: Map<String, String>,
    val description: Map<String, String>
) {
    fun getTitle(): String {
        val locale = Locale.getDefault()
        return title[locale.language.toLowerCase()] ?: title["default"] ?: ""
    }

    fun getDescription(): String {
        val locale = Locale.getDefault()
        return description[locale.language.toLowerCase()] ?: description["default"] ?: ""
    }
}
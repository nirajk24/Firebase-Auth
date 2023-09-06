package com.affinity.repsoft.repo

import android.content.Context
import android.preference.PreferenceManager

class MyPreference(context: Context?) {

    companion object {
        private const val USERNAME = "USERNAME"
        private const val IS_REMEMBER = "IS_REMEMBER"

    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var username = preferences.getString(USERNAME, "")
        set(value) = preferences.edit().putString(USERNAME, value).apply()

    var isRemember = preferences.getBoolean(IS_REMEMBER, false)
        set(value) = preferences.edit().putBoolean(IS_REMEMBER, value).apply()


}
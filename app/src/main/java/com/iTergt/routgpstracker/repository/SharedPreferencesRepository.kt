package com.iTergt.routgpstracker.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val USER_PREFERENCES = "user_pref"
private const val PREFERENCES = "app_preferences"
private const val LOGIN = "is_logged_in"
private const val FIREBASE_USER_UID = "user_Uid"

class SharedPreferencesRepository(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val userPreferences: SharedPreferences =
        context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(LOGIN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.edit { putBoolean(LOGIN, isLoggedIn) }
    }

    fun getUserUid(): String? {
        return userPreferences.getString(FIREBASE_USER_UID, "")
    }

    fun setUserUid(uid: String) {
        userPreferences.edit { putString(FIREBASE_USER_UID, uid) }
    }

    fun clearUserData() {
        userPreferences.edit {
            clear()
        }
    }
}
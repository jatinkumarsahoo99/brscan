package vraj.app.cancerscreening.screening.data.helper

import android.content.Context
import vraj.app.cancerscreening.screening.data.constant.AppConstant

class PreferenceManager {
    fun setValue(context: Context, strKey: String, strValue: String) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(strKey, strValue)
            apply()
        }
    }

    fun getValue(context: Context, strKey: String, defaultValue: String): String {
        val sharedPref = context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE)
            ?: return defaultValue
        val strValue = sharedPref.getString(strKey, defaultValue).toString()
        return strValue
    }

    fun setValue(context: Context, strKey: String, value: Int) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(strKey, value)
            apply()
        }
    }

    fun getValue(context: Context, strKey: String, defaultValue: Int): Int {
        val sharedPref = context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE)
            ?: return defaultValue
        val strValue = sharedPref.getInt(strKey, defaultValue)
        return strValue
    }

    fun setValue(context: Context, strKey: String, value: Boolean) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(strKey, value)
            apply()
        }
    }

    fun getValue(context: Context, strKey: String, defaultValue: Boolean): Boolean {
        val sharedPref = context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE)
            ?: return defaultValue
        val strValue = sharedPref.getBoolean(strKey, defaultValue)
        return strValue
    }

    fun setValue(context: Context, strKey: String, value: Long) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putLong(strKey, value)
            apply()
        }
    }

    fun getValue(context: Context, strKey: String, defaultValue: Long): Long {
        val sharedPref = context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE)
            ?: return defaultValue
        val strValue = sharedPref.getLong(strKey, defaultValue)
        return strValue
    }

    fun setValue(context: Context, strKey: String, value: Float) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putFloat(strKey, value)
            apply()
        }
    }

    fun getValue(context: Context, strKey: String, defaultValue: Float): Float {
        val sharedPref = context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE)
            ?: return defaultValue
        val strValue = sharedPref.getFloat(strKey, defaultValue)
        return strValue
    }

    fun clearPreference(context: Context) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit())
        {
            clear()
            apply()
        }
    }

    fun clearPreference(context: Context, strKey: String) {
        val sharedPref =
            context.getSharedPreferences(AppConstant.APP_PREF, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit())
        {
            remove(strKey)
            apply()
        }
    }
}
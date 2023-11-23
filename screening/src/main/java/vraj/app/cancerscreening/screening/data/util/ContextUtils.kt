package vraj.app.cancerscreening.screening.data.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {

        fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
            var context = c
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(localeToSwitchTo)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.locale = localeToSwitchTo
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            return ContextUtils(context)
        }


        fun getSelectedLanguage(position: Int): String
        {
            var strSelectedLang = ""

            when(position)
            {
                0 ->
                    strSelectedLang = "en"
                1 ->
                    strSelectedLang = "gu"
                2 ->
                    strSelectedLang = "hi"
                3 ->
                    strSelectedLang = "ja"
                4 ->
                    strSelectedLang = "ar"
                5 ->
                    strSelectedLang = "et"
                6 ->
                    strSelectedLang = "de"
                7 ->
                    strSelectedLang = "zh"
                8 ->
                    strSelectedLang = "ko"
            }

            return strSelectedLang
        }

        fun getAppType() : Boolean{
            return true
        }
    }
}
package vraj.app.cancerscreening.screening.ui.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.data.helper.PreferenceManager
import vraj.app.cancerscreening.screening.data.util.ContextUtils
import vraj.app.cancerscreening.screening.R
import java.io.File
import java.util.*

abstract class BaseActivity: AppCompatActivity() {

    val preferenceManager = PreferenceManager()

    /*override fun attachBaseContext(newBase: Context) {
        // get chosen language from shread preference
        //val localeToSwitchTo = preferenceManager.getValue(newBase, PREF_USER_LANGUAGE, "")

        *//*if(localeToSwitchTo.isNotEmpty())
        {
            val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, Locale(localeToSwitchTo))
            super.attachBaseContext(localeUpdatedContext)
        }*//*
    }*/

    fun replaceFragmenty(fragment: Fragment,allowStateLoss: Boolean = false, @IdRes containerViewId: Int) {
        supportFragmentManager.beginTransaction().replace(containerViewId, fragment).commit()
        if(listener != null){
            listener!!.onFragmentReplaced(fragment)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public fun setBitmapFromPath(path : String,iv_profile_pic : ImageView){
        val userId = preferenceManager.getValue(this, AppConstant.PREF_USER_ID,"");
        val filePath = path.replace(userId,"")
        val fileProfilePic = File(filePath,userId)
        if(path.isNotEmpty() && fileProfilePic.exists()) {
            iv_profile_pic.setImageBitmap(
                BitmapFactory.decodeFile(
                    fileProfilePic.absolutePath,
                    BitmapFactory.Options()
                )
            )
        } else {
            iv_profile_pic.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_avtar))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if(ContextUtils.getAppType())ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    var listener : OnFragmentReplacedListener? = null

    interface OnFragmentReplacedListener{
        fun onFragmentReplaced(fragment: Fragment)

    }
}
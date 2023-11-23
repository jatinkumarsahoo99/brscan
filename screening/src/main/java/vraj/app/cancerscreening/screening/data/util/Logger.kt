package vraj.app.cancerscreening.screening.data.util

import android.util.Log

object Logger {

    public fun showErrorLog(message : String){
        Log.e("TAG","BR-SCAN : $message")
    }
}
package vraj.app.cancerscreening.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.ui.activities.DemoActivity


class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val receivedIntent = intent
        if (receivedIntent != null) {
            val action = receivedIntent.action
            val type = receivedIntent.type

            if (Intent.ACTION_SEND == action && type != null) {
                if ("text/plain" == type) {
                    val receivedText = receivedIntent.getStringExtra("patient_name")
//                    Log.e("TAGBRScanJKs", receivedText.toString())
                    if (receivedText != null) {
                        Log.i("TAGBRScanJKs", receivedText.toString())
//                        print("My data is>>>" + receivedText)
                    }
                }
            }
        }
        onCreateCode(receivedIntent)
    }

    private fun onCreateCode(receivedIntent: Intent) {

        (Handler()).postDelayed({
            /* print("Here is my data?>>>  "+intent.getStringExtra("patient_name").toString())*/
            gotToNextScreen(receivedIntent)
        }, SPLASH_TIME_OUT)

    }

    @SuppressLint("SdCardPath")
    private fun gotToNextScreen(receivedIntent: Intent) {
        val intent = Intent(this, DemoActivity::class.java)
        intent.putExtra(AppConstant.KEY_PATIENT_NAME,"James Bond")
        intent.putExtra(AppConstant.KEY_PATIENT_GENDER,"Male")
        intent.putExtra(AppConstant.KEY_PATIENT_WEIGHT,"67")
        intent.putExtra(AppConstant.KEY_PATIENT_HEIGHT,"176")
        intent.putExtra(AppConstant.KEY_PATIENT_HEIGHT,"176")
        intent.putExtra(AppConstant.KEY_SAVE_PATH,"/sdcard/Documents/")
        intent.putExtra(AppConstant.KEY_HOSPITAL_LOGO,"http://13.127.75.121/android/Br_Scan/uploads/hospital_logo/Meenal_63fa371d0b8f2.jpg")
        intent.putExtra(AppConstant.KEY_USER_ID,"James_25634")
        intent.putExtra(AppConstant.KEY_PATIENT_ID,"James_25634")
        intent.putExtra(AppConstant.KEY_HOSPITAL_DOC_NAME,"Dr. Sethi")
        intent.putExtra(AppConstant.KEY_HOSPITAL_DOC_SPECIALIZATION,"Cardio Thorasic")
        intent.putExtra(AppConstant.KEY_HOSPITAL_DOC_DESIGNATION,"Medical Director")
        startActivity(intent)
        finish()
    }
}
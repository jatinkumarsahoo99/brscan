package vraj.app.cancer.screening

import android.app.Application

class BrScanApp : Application() {
    private val TAG: String = BrScanApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        //initializeMLInstant()
    }


    /*fun initializeMLInstant()
    {
        val options = TfLiteInitializationOptions.builder()
            .setEnableGpuDelegateSupport(true)
            .build()

        TfLiteVision.initialize(applicationContext, options).addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener {
            // Called if the GPU Delegate is not supported on the device
            TfLiteVision.initialize(applicationContext).addOnSuccessListener {
                objectDetectorListener.onInitialized()
            }.addOnFailureListener{
                objectDetectorListener.onError("TfLiteVision failed to initialize: "
                        + it.message)
            }
        }
    }*/

    /*fun getContext(): Context {
        return applicationContext
    }*/
}
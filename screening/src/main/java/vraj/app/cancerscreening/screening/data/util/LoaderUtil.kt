package vraj.app.cancerscreening.screening.data.util

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import vraj.app.cancerscreening.screening.R

object LoaderUtil
{
    private val TAG: String = "LoaderUtils"
    private lateinit var progressDialogBuilder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    /**
     * @param context
     * @action show progress loader
     */
    fun showLoading(context: Context, isCancellable: Boolean, strMessage: String){
        progressDialogBuilder = AlertDialog.Builder(context)
        progressDialogBuilder.setCancelable(isCancellable) // if you want user to wait for some process to finish,
        progressDialogBuilder.setView(R.layout.layout_loading_dialog)
        progressDialogBuilder.setMessage(strMessage)

        progressDialog = progressDialogBuilder.create()
        progressDialog.show()
    }

    /**
     * @action hide progress loader
     */
    fun hideLoading(){
        try {
            progressDialog.dismiss()
        }catch (ex: java.lang.Exception){
            Log.e(TAG, ex.toString())
        }
    }

    /**
     * @param context
     * @action show Long toast message
     */
    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
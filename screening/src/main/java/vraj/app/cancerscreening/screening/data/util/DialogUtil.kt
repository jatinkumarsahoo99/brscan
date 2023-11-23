package vraj.app.cancerscreening.screening.data.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import vraj.app.cancerscreening.screening.R


object DialogUtil {

    interface OnNegativeButtonClickListner {
        fun onNegativeButtonClick()
    }

    interface OnPositiveButtonClickListener {
        fun onButtonClick()
    }

    private var currentDialog: Dialog? = null
    private var positiveButtonClickListener: OnPositiveButtonClickListener? = null
    private var negativeButtonClickListener: OnNegativeButtonClickListner? = null


    fun showToastMessage(context: Context, strToastMessage: String) {
        val toast = Toast.makeText(context, strToastMessage, LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }

    fun dismissCurrentDialog() {
        if (currentDialog != null && currentDialog!!.isShowing) currentDialog!!.dismiss()
    }

    fun showHyperPositiveDialog(context: Context, errorMsg: String?) {
        dismissCurrentDialog()
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_positive_button)
        val btnDialogYes: Button = dialog.findViewById(R.id.btn_confirm_yes)
        val txtMessage = dialog.findViewById<TextView>(R.id.txt_confirm_message)
        txtMessage.text = Html.fromHtml(errorMsg)
        txtMessage.movementMethod = LinkMovementMethod.getInstance()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnDialogYes.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        currentDialog = dialog
    }

    fun showHyperPositiveDialog(
        context: Context,
        errorMsg: String?,
        click: OnPositiveButtonClickListener
    ) {
        showHyperPositiveDialog(context, errorMsg, click, true)
    }

    fun showHyperPositiveDialog(
        context: Context,
        errorMsg: String?,
        click: OnPositiveButtonClickListener,
        isCancelable: Boolean
    ) {
        dismissCurrentDialog()
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_positive_button)
        val btnDialogYes: Button = dialog.findViewById(R.id.btn_confirm_yes)
        val txtMessage = dialog.findViewById<TextView>(R.id.txt_confirm_message)
        txtMessage.text = Html.fromHtml(errorMsg)
        txtMessage.movementMethod = LinkMovementMethod.getInstance()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(isCancelable)
        btnDialogYes.setOnClickListener {
            click.onButtonClick()
            dialog.dismiss()
        }
        dialog.show()
        currentDialog = dialog
    }


    fun showPositiveAndNegativeDialog(
        context: Context,
        message: String?,
        click: OnPositiveButtonClickListener
    ) {
        dismissCurrentDialog()
        val dialog = Dialog(context)
        positiveButtonClickListener = click
        dialog.setContentView(R.layout.dialog_exit)
        dialog.show()
        currentDialog = dialog
        val btnDialogYes: Button = dialog.findViewById(R.id.btn_confirm_yes)
        val btnDialogNo: Button = dialog.findViewById(R.id.btn_confirm_no)
        val txtMessage = dialog.findViewById<TextView>(R.id.txt_confirm_message)
        txtMessage.text = message
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnDialogYes.setOnClickListener {
            click.onButtonClick()
            dialog.dismiss()
        }
        btnDialogNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun showHyperPositiveAndNegativeDialog(context: Context?, message: String?,
                                           click: OnPositiveButtonClickListener, negativeButtonClick: OnNegativeButtonClickListner
    ) {
        dismissCurrentDialog()
        val dialog = Dialog(context!!)
        positiveButtonClickListener = click
        negativeButtonClickListener = negativeButtonClick
        dialog.setContentView(R.layout.dialog_exit)
        dialog.show()
        currentDialog = dialog
        val btnDialogYes = dialog.findViewById<Button>(R.id.btn_confirm_yes)
        val btnDialogNo = dialog.findViewById<Button>(R.id.btn_confirm_no)
        val txtMessage = dialog.findViewById<TextView>(R.id.txt_confirm_message)
        txtMessage.text = message
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        btnDialogYes.setOnClickListener {
            if (positiveButtonClickListener != null)
                positiveButtonClickListener!!.onButtonClick()
            dialog.dismiss()
        }
        btnDialogNo.setOnClickListener {
            if (negativeButtonClickListener != null)
                negativeButtonClickListener!!.onNegativeButtonClick()
            dialog.dismiss()
        }
    }
}
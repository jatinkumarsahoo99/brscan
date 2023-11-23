package vraj.app.cancer.screening.ui.fragments

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.ui.activities.BaseActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

abstract class BaseFragment  : Fragment(){

    public fun closeAll(){
        requireActivity().finishAffinity()
    }

    fun saveToInternalStorage(uri : Uri): String? {
        val imageUri: Uri = uri
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        val cw = ContextWrapper(requireContext())
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, (requireActivity() as BaseActivity).preferenceManager.getValue(requireContext(),
            AppConstant.PREF_USER_ID,""))
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }
}
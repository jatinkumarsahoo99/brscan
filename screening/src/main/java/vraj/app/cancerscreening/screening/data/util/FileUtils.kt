package vraj.app.cancerscreening.screening.data.util

import android.content.Context
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import java.io.File
import java.io.IOException

class FileUtils
{
    fun preparePdfDirectory(mContext: Context, userId: String): File {
        val storageDir: String = AppConstant.strSaveReportPath
        val folder = File(storageDir + File.separator + "Report")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val fileName = userId + "_" + System.currentTimeMillis() + ".pdf"
        val file = File(folder, fileName)
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}
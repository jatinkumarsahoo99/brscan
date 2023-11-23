package vraj.app.cancerscreening.screening.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import vraj.app.cancer.screening.BrScanApp


class ImageUtil(private val mContext: Context) {

    private val RATIO = 1

    fun getBitmapFromRes(res: Int): Bitmap {
        val drawableRes: Drawable = mContext.resources.getDrawable(res)
        val bitDw = drawableRes as BitmapDrawable
        return bitDw.bitmap
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > RATIO) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}
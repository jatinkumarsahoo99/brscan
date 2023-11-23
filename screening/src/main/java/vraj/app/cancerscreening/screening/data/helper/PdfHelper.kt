@file:Suppress("DEPRECATION")

package vraj.app.cancerscreening.screening.data.helper


import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.text.TextPaint
import com.bumptech.glide.util.LruCache
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.LINE_MARGIN
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_FOOTER_BOTTOM
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_HEIGHT_A4
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_PADDING_LEFT
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_PADDING_RIGHT
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_PADDING_TOP
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_PADDING_TOTAL
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PAGE_WIDTH_A4
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PREF_USER_ID
import vraj.app.cancerscreening.screening.data.util.DateUtil
import vraj.app.cancerscreening.screening.data.util.DateUtil.REPORT_DATE_FORMAT
import vraj.app.cancerscreening.screening.data.util.DateUtil.REPORT_TIME_FORMAT
import vraj.app.cancerscreening.screening.data.util.ImageUtil
import java.io.File
import java.io.FileOutputStream
import java.util.*


class PdfHelper(private val mContext: Context) {

    private var contentLength = PAGE_PADDING_TOP
    private lateinit var document: PdfDocument
    private lateinit var page: PdfDocument.Page

    private val fmHeaderLabel = Paint.FontMetrics()
    private val fmFooterLabel = Paint.FontMetrics()
    private val bottomLabelBottom = PAGE_HEIGHT_A4 - PAGE_FOOTER_BOTTOM
    private var bottomLabelTop = 0f

    private val txtSize: Float = mContext.resources.getDimension(R.dimen.text_pdf_details)
    private val txtAlign: Align = Align.LEFT
    private val fontType: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    private val xPos: Float = PAGE_PADDING_TOTAL.toFloat()
    private lateinit var mImgLogo: Bitmap
    /*private lateinit var apiUser : DataPatient*/
    private var userId : String =""
    private var patientId : String =""

    fun generatePatientDocument(
        pdfDirectory: File,
        bitmapLruCache: LruCache<String, Bitmap>,
        hospitalLogo: Bitmap
    ) {
        mImgLogo = hospitalLogo
        try {
            val fileOutputStream = FileOutputStream(pdfDirectory)

            document = PdfDocument()
            contentLength = PAGE_PADDING_TOP

            patientId = AppConstant.strPatientId
            userId = PreferenceManager().getValue(mContext, PREF_USER_ID, "asdf")

            page = getNewPage()
            //addWaterMark()
            manageHeaderData()
            addHospitalCopyRightText()
            managePatientData()
            addLeftImagesSection(bitmapLruCache)
            addRightImagesSection(bitmapLruCache)
            addRemarkText("Remarks : ", 190)
            addEndReportText(mContext.getString(R.string.pdf_end_report), 70)
            addEndReportText(mContext.getString(R.string.pdf_end_report_line_2), 50)
            document.finishPage(page)

            document.writeTo(fileOutputStream)
            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun manageHeaderData() {
        addHeaderImage()
        addHeader(
            mContext.resources.getString(R.string.pdf_title),
            (page.canvas.width / 2).toFloat(),
            (contentLength + PAGE_PADDING_TOP + LINE_MARGIN + 10).toFloat(),
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
            Align.CENTER,
            mContext.resources.getDimension(R.dimen.text_pdf_title),
            mContext.resources.getColor(R.color.pdfLabelColor)
        )

        contentLength += PAGE_PADDING_TOP + LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()
    }

    private fun managePatientData() {
        if (userId.trim().isNotEmpty()) {

            val totalReport = 1

            val reportNo = String.format(
                mContext.resources.getString(R.string.pdf_report_no),
                totalReport + 1
            )
            val userName = String.format(
                mContext.resources.getString(R.string.pdf_patient_name),
                AppConstant.strPatientName
            )
            val userDob = String.format(
                mContext.resources.getString(R.string.pdf_user_age),
                AppConstant.strPatientAge
            )
            val userGender = String.format(
                mContext.resources.getString(R.string.pdf_user_sex),
                AppConstant.strPatientGender
            )
            val userWeight =
                String.format(mContext.resources.getString(R.string.pdf_user_weight), AppConstant.strPatientWeight)
            val userHeight =
                String.format(mContext.resources.getString(R.string.pdf_user_height), AppConstant.strPatientHeight)
            val reportDate = String.format(
                mContext.resources.getString(R.string.pdf_report_date),
                DateUtil.getDate(Calendar.getInstance(), REPORT_DATE_FORMAT)
            )
            val reportTime = String.format(
                mContext.resources.getString(R.string.pdf_report_time),
                DateUtil.getDate(Calendar.getInstance(), REPORT_TIME_FORMAT)
            )

            addHeader(
                "$reportDate $reportTime",
                (page.canvas.width - PAGE_PADDING_RIGHT).toFloat(),
                contentLength.toFloat(),
                fontType,
                Align.RIGHT,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()
            addHeader(
                reportNo,
                xPos,
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.pdfLabelColor)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()
            addHeader(
                mContext.resources.getString(R.string.pdf_subject_details),
                PAGE_PADDING_TOTAL.toFloat(),
                contentLength.toFloat(),
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                Align.LEFT,
                mContext.resources.getDimension(R.dimen.text_pdf_details),
                mContext.resources.getColor(R.color.pdfLabelColor)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()

            addHeader(
                userName,
                PAGE_PADDING_TOTAL.toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()

            addHeader(
                userDob,
                PAGE_PADDING_TOTAL.toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )
            addHeader(
                userGender,
                xPos + (page.canvas.width /5/*2*/).toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            addHeader(
                "Doctor : ${AppConstant.strHospitalDocName} ${AppConstant.strHospitalDocSpecialization}",
                xPos + (page.canvas.width / 2).toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()
            addHeader(
                userWeight,
                /*xPos + (page.canvas.width / 5).toFloat()*/PAGE_PADDING_TOTAL.toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )
            addHeader(
                userHeight,
                xPos + (page.canvas.width / /*2*/5).toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )
            addHeader(
                "Designation : ${AppConstant.strHospitalDocDesignation}",
                xPos + (page.canvas.width / 2).toFloat(),
                contentLength.toFloat(),
                fontType,
                txtAlign,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            contentLength += LINE_MARGIN + 10 - fmHeaderLabel.top.toInt()
        }
    }

    private fun addLeftImagesSection(bitmapLruCache: LruCache<String, Bitmap>) {
        contentLength += PAGE_PADDING_TOP
        addHeader(
            mContext.resources.getString(R.string.pdf_scan_images),
            PAGE_PADDING_TOTAL.toFloat(),
            contentLength.toFloat(),
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
            Align.LEFT,
            mContext.resources.getDimension(R.dimen.text_pdf_details),
            mContext.resources.getColor(R.color.pdfLabelColor)
        )

        contentLength += LINE_MARGIN - fmHeaderLabel.top.toInt()
        addHeader(
            mContext.resources.getString(R.string.pdf_left_breast),
            (page.canvas.width / 2).toFloat(),
            contentLength.toFloat(),
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
            Align.CENTER,
            mContext.resources.getDimension(R.dimen.text_pdf_details),
            mContext.resources.getColor(R.color.black)
        )

        contentLength += LINE_MARGIN - fmHeaderLabel.top.toInt()
        contentLength += PAGE_PADDING_TOP

        if (bitmapLruCache.currentSize >= 1) {
            val bmpFirstImage = ImageUtil(mContext).getResizedBitmap(
                (bitmapLruCache.get("0"))!!,
                (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
            )

            addHeader(
                mContext.resources.getString(R.string.pdf_top_side),
                (PAGE_PADDING_LEFT + (bmpFirstImage.width / 2)).toFloat(),
                contentLength.toFloat(),
                fontType,
                Align.CENTER,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            page.canvas.drawBitmap(
                bmpFirstImage,
                PAGE_PADDING_LEFT.toFloat(),
                (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(),
                null
            )


            if (bitmapLruCache.currentSize >= 2) {
                val bmpSecondImage = ImageUtil(mContext).getResizedBitmap(
                    (bitmapLruCache.get("1"))!!,
                    (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
                )

                addHeader(
                    mContext.resources.getString(R.string.pdf_left_side),
                    (PAGE_PADDING_LEFT + bmpFirstImage.width + (bmpSecondImage.width / 2)).toFloat(),
                    contentLength.toFloat(),
                    fontType,
                    Align.CENTER,
                    txtSize,
                    mContext.resources.getColor(R.color.black)
                )

                page.canvas.drawBitmap(
                    bmpSecondImage,
                    (PAGE_PADDING_LEFT + 10 + bmpFirstImage.width).toFloat(),
                    (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(), null
                )


                if (bitmapLruCache.currentSize >= 3) {
                    val bmpThirdImage = ImageUtil(mContext).getResizedBitmap(
                        (bitmapLruCache.get("2"))!!,
                        (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
                    )

                    addHeader(
                        mContext.resources.getString(R.string.pdf_right_side),
                        (PAGE_PADDING_LEFT + bmpFirstImage.width + bmpSecondImage.width + (bmpThirdImage.width / 2)).toFloat(),
                        contentLength.toFloat(),
                        fontType,
                        Align.CENTER,
                        txtSize,
                        mContext.resources.getColor(R.color.black)
                    )

                    page.canvas.drawBitmap(
                        bmpThirdImage,
                        (PAGE_PADDING_LEFT + 20 + bmpFirstImage.width + bmpSecondImage.width).toFloat(),
                        (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(),
                        null
                    )
                }

                contentLength += bmpFirstImage.height + PAGE_PADDING_LEFT
            }
        }
    }

    private fun addRightImagesSection(bitmapLruCache: LruCache<String, Bitmap>) {
        contentLength += PAGE_PADDING_TOP + 30
        addHeader(
            mContext.resources.getString(R.string.pdf_right_breast),
            (page.canvas.width / 2).toFloat(),
            contentLength.toFloat(),
            Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
            Align.CENTER,
            mContext.resources.getDimension(R.dimen.text_pdf_details),
            mContext.resources.getColor(R.color.black)
        )

        contentLength += LINE_MARGIN - fmHeaderLabel.top.toInt()
        contentLength += PAGE_PADDING_TOP

        if (bitmapLruCache.currentSize >= 4) {
            val bmpFifthImage = ImageUtil(mContext).getResizedBitmap(
                (bitmapLruCache.get("3"))!!,
                (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
            )

            addHeader(
                mContext.resources.getString(R.string.pdf_top_side),
                (PAGE_PADDING_LEFT + (bmpFifthImage.width / 2)).toFloat(),
                contentLength.toFloat(),
                fontType,
                Align.CENTER,
                txtSize,
                mContext.resources.getColor(R.color.black)
            )

            page.canvas.drawBitmap(
                bmpFifthImage,
                PAGE_PADDING_LEFT.toFloat(),
                (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(),
                null
            )

            if (bitmapLruCache.currentSize >= 5) {
                val bmpSixthImage = ImageUtil(mContext).getResizedBitmap(
                    (bitmapLruCache.get("4"))!!,
                    (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
                )

                addHeader(
                    mContext.resources.getString(R.string.pdf_left_side),
                    (PAGE_PADDING_LEFT + bmpFifthImage.width + (bmpSixthImage.width / 2)).toFloat(),
                    contentLength.toFloat(),
                    fontType,
                    Align.CENTER,
                    txtSize,
                    mContext.resources.getColor(R.color.black)
                )

                page.canvas.drawBitmap(
                    bmpSixthImage,
                    (PAGE_PADDING_LEFT + 10 + bmpFifthImage.width).toFloat(),
                    (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(), null
                )

                if (bitmapLruCache.currentSize >= 6) {
                    val bmpSeventhImage = ImageUtil(mContext).getResizedBitmap(
                        (bitmapLruCache.get("5"))!!,
                        (PAGE_WIDTH_A4 - (PAGE_PADDING_TOTAL + (PAGE_PADDING_LEFT / 2))) / 3
                    )

                    addHeader(
                        mContext.resources.getString(R.string.pdf_right_side),
                        (PAGE_PADDING_LEFT + bmpFifthImage.width + bmpSixthImage.width + (bmpSeventhImage.width / 2)).toFloat(),
                        contentLength.toFloat(),
                        fontType,
                        Align.CENTER,
                        txtSize,
                        mContext.resources.getColor(R.color.black)
                    )

                    page.canvas.drawBitmap(
                        bmpSeventhImage,
                        (PAGE_PADDING_LEFT + 20 + bmpFifthImage.width + bmpSixthImage.width).toFloat(),
                        (contentLength + LINE_MARGIN - fmHeaderLabel.top.toInt()).toFloat(),
                        null
                    )
                }
            }
        }
    }

    private fun addHeader(
        text: String, x: Float, y: Float, typeface: Typeface,
        align: Align, textSize: Float, color: Int
    ) {

        val textPaint = TextPaint()
        textPaint.typeface = typeface
        textPaint.textSize = textSize
        textPaint.textAlign = align
        textPaint.color = color

        textPaint.getFontMetrics(fmHeaderLabel)
        page.canvas.drawText(text, x, y, textPaint)
    }


    private fun getNewPage(): PdfDocument.Page {
        val pageInfo = PageInfo.Builder(PAGE_WIDTH_A4, PAGE_HEIGHT_A4, 1).create()
        return document.startPage(pageInfo)
    }

    private fun addHeaderImage() {
        val textPaint = TextPaint()
        textPaint.color = mContext.resources.getColor(R.color.pdfTopBgColor)
        page.canvas.drawRect(0f, 0f, (page.canvas.width / 2).toFloat(), 60f, textPaint)
        page.canvas.drawRect(0f, 70f, (page.canvas.width / 2.5).toFloat(), 90f, textPaint)
        val bmpRightImage: Bitmap = ImageUtil(mContext)
            .getResizedBitmap(
                mImgLogo, 50
            )
        page.canvas.drawBitmap(
            bmpRightImage,
            (page.canvas.width - (PAGE_PADDING_RIGHT + bmpRightImage.width)).toFloat(),
            PAGE_PADDING_TOP.toFloat(), null
        )
        contentLength += bmpRightImage.height
    }

    private fun addEndReportText(endReportText: String, verticalSpacing: Int) {
        val textPaint = TextPaint()
        textPaint.textSize = mContext.resources.getDimension(R.dimen.text_pdf_bottom)
        textPaint.color = mContext.resources.getColor(R.color.black)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textAlign = Align.CENTER
        textPaint.getFontMetrics(fmFooterLabel)
        val endReportY =
            (page.canvas.height + fmFooterLabel.top).toFloat() - PAGE_FOOTER_BOTTOM - verticalSpacing
        page.canvas.drawText(
            endReportText, (page.canvas.width / 2).toFloat(),
            endReportY, textPaint
        )
    }

    private fun addRemarkText(endReportText: String, verticalSpacing: Int) {
        val textPaint = TextPaint()
        textPaint.textSize = mContext.resources.getDimension(R.dimen.text_pdf_bottom)
        textPaint.color = mContext.resources.getColor(R.color.black)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textAlign = Align.LEFT
        textPaint.getFontMetrics(fmFooterLabel)

        var endReportY =
            (page.canvas.height + fmFooterLabel.top).toFloat() - PAGE_FOOTER_BOTTOM - verticalSpacing
        page.canvas.drawText(
            endReportText, 50f,
            endReportY, textPaint
        )
        val paint = Paint()
        val bounds = Rect()
        paint.getTextBounds(endReportText, 0, endReportText.length, bounds);
        endReportY += 10

        page.canvas.drawLine(
            90f+bounds.width(),
            endReportY,page.canvas.width.toFloat()-50,endReportY,textPaint)

        endReportY += 50

        page.canvas.drawLine(
            50f,
            endReportY,page.canvas.width.toFloat()-50,endReportY,textPaint)

    }

    private fun addHospitalCopyRightText() {
        val copyRightText: String = mContext.getString(R.string.pdf_copyright)
        val textPaint = TextPaint()
        textPaint.textSize = mContext.resources.getDimension(R.dimen.text_pdf_bottom)
        textPaint.color = mContext.resources.getColor(R.color.pdfBottomBgColor)
        textPaint.textAlign = Align.RIGHT
        textPaint.getFontMetrics(fmFooterLabel)

        val bmpLeftImage: Bitmap = ImageUtil(mContext)
            .getResizedBitmap(
                BitmapFactory.decodeResource(
                    mContext.resources,
                    R.drawable.ic_sub_logo
                ), 150
            )

        bottomLabelTop = (page.canvas.height - bmpLeftImage.height).toFloat() - PAGE_FOOTER_BOTTOM
        page.canvas.drawRect(
            0f, bottomLabelTop, page.canvas.width.toFloat(),
            page.canvas.height.toFloat(), textPaint
        )

        textPaint.color = mContext.resources.getColor(R.color.black)

        page.canvas.drawBitmap(
            bmpLeftImage, (page.canvas.width - PAGE_FOOTER_BOTTOM).toFloat() - bmpLeftImage.width,
            (page.canvas.height - bmpLeftImage.height).toFloat(), null
        )
        page.canvas.drawText(
            copyRightText, (page.canvas.width - PAGE_FOOTER_BOTTOM - bmpLeftImage.width - 20).toFloat(),
            bottomLabelBottom.toFloat(), textPaint
        )
    }

}
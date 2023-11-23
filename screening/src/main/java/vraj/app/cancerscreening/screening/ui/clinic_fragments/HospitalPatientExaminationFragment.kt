package vraj.app.cancerscreening.screening.ui.clinic_fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import com.bumptech.glide.util.LruCache
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.render.env.RotateType
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.oginotihiro.cropview.CropUtil
import com.oginotihiro.cropview.CropView
import vraj.app.cancerscreening.screening.bluetooth.SerialListener
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.data.helper.PdfHelper
import vraj.app.cancerscreening.screening.data.util.FileUtils
import vraj.app.cancerscreening.screening.data.util.LoaderUtil
import vraj.app.cancerscreening.screening.ui.fragments.EndProcedureFragment
import vraj.app.cancerscreening.screening.bluetooth.SerialService
import vraj.app.cancerscreening.screening.data.helper.TextUtil
import vraj.app.cancerscreening.screening.databinding.FragmentPatientExaminationBinding
import vraj.app.cancerscreening.screening.formatter.PushDownAnim
import vraj.app.cancerscreening.screening.data.util.DialogUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.bluetooth.SerialSocket


class HospitalPatientExaminationFragment : CameraFragment(), DialogUtil.OnPositiveButtonClickListener,
    ServiceConnection, SerialListener {

    private lateinit var root: View
    private lateinit var txtMainTitle: TextView
    private lateinit var imgTakenImage: ImageView
    private lateinit var imgDeleteImage: ImageButton
    private lateinit var btnCaptureImage: ImageButton
    private lateinit var cropView: CropView
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private var mCounter: Int = 0
    private var isCameraStart: Boolean = false
    private val captureImageFileList = ArrayList<String>()
    private var userId = ""
    private var deviceAddress: String? = null

    private lateinit var ivMainHeaderImage: ImageView
    private lateinit var previewView: PreviewView

    /** For Image Cropping**/
    private var croppedBitmap: Bitmap? = null
    private var destination :Uri? = null
    private var mCurrentPosition: Int = 0

    /*For Bluetooth connection*/
    private var service: SerialService? = null
    private var initialStart = true

    private enum class Connected {
        False, Pending, True
    }

    private var connected = Connected.False
    private val hexEnabled = false
    private var pendingNewline = false
    private val newline = TextUtil.newline_crlf

    private lateinit var mCameraClient: CameraClient
    private val TAG = "ExaminationFragment"
    private var previewWidth : Int  = 0
    private var previewHeight : Int = 0;
    private lateinit var mViewBinding : FragmentPatientExaminationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceAddress = requireArguments().getString("device")
        activity?.setTitle(R.string.title_self_examination)
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service!!.attach(this) else requireActivity().startService(
            Intent(
                activity,
                SerialService::class.java
            )
        ) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    override fun onStop() {
        if (service != null && !requireActivity().isChangingConfigurations) service!!.detach()
        super.onStop()
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        requireActivity().bindService(
            Intent(getActivity(), SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDetach() {
        try {
            requireActivity().unbindService(this)
        } catch (ignored: java.lang.Exception) {
        }
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        if (initialStart && service != null) {
            initialStart = false
            requireActivity().runOnUiThread { connect() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCounter = 0
        isCameraStart = false
        initUi()
        manageClickEvents()
        addImagePaths()
        manageTextData()

        PushDownAnim.setPushDownAnimTo(btnNext)
            .setScale(PushDownAnim.MODE_STATIC_DP, 5F)
            .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
            .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
            .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
            .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR)

        PushDownAnim.setPushDownAnimTo(btnPrev)
            .setScale(PushDownAnim.MODE_STATIC_DP, 5F)
            .setDurationPush(PushDownAnim.DEFAULT_PUSH_DURATION)
            .setDurationRelease(PushDownAnim.DEFAULT_RELEASE_DURATION)
            .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
            .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR)
    }

    private fun removeBond(device: BluetoothDevice) {
        try {
            device::class.java.getMethod("removeBond").invoke(device)
        } catch (e: Exception) {
            Log.e("TAG", "Removing bond has been failed. ${e.message}")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if(device != null){
            removeBond(device!!)
        }
        // Shut down our background executor
        //cameraExecutor.shutdown()
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")

    private fun initUi() {
        previewView = root.findViewById(R.id.sv_capture_view)

        btnCaptureImage = root.findViewById(R.id.ib_capture_image)
        btnNext = root.findViewById(R.id.btn_next_capture)
        btnPrev = root.findViewById(R.id.btn_prev_capture)

        imgTakenImage = root.findViewById(R.id.img_captured_image)
        imgDeleteImage = root.findViewById(R.id.btn_recapture)

        ivMainHeaderImage = root.findViewById(R.id.iv_main_header_image)
        txtMainTitle = root.findViewById(R.id.tv_main_title)
        cropView = root.findViewById(R.id.cropView)

        btnNext.visibility = INVISIBLE
        btnPrev.visibility = INVISIBLE


        previewView.doOnLayout {
            it.measuredWidth
            it.measuredHeight
            previewHeight = it.measuredHeight
            previewWidth = it.measuredWidth
            Log.e(TAG,"width : ${it.measuredWidth} : height : ${it.measuredHeight}")
        }


    }

    private fun manageClickEvents() {
        btnCaptureImage.setOnClickListener {
            btnPrev.visibility = INVISIBLE
            LoaderUtil.showLoading(requireActivity(), false, getString(R.string.capturing_image))
            onCaptureClick()
        }

        btnNext.setOnClickListener {
            onNextClick()
        }

        btnPrev.setOnClickListener {
            onPrevClick()
        }

        imgDeleteImage.setOnClickListener {
            onDeleteClick()
        }
    }

    private fun addImagePaths() {
        captureImageFileList.add(0, "")
        captureImageFileList.add(1, "")
        captureImageFileList.add(2, "")
        captureImageFileList.add(3, "")
        captureImageFileList.add(4, "")
        captureImageFileList.add(5, "")
        captureImageFileList.add(6, "")
        captureImageFileList.add(7, "")
    }

    private fun manageTextData() {
        if (mCounter <= 0)
            btnPrev.visibility = INVISIBLE
        else
            btnPrev.visibility = VISIBLE

        when (mCounter) {
            0 -> {
                txtMainTitle.setText(R.string.title_step_3)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_3_label)
            }
            1 -> {
                txtMainTitle.setText(R.string.title_step_4)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_4_label)
            }
            2 -> {
                txtMainTitle.setText(R.string.title_step_5)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_5_label)
            }
            3 -> {
                txtMainTitle.setText(R.string.title_step_6)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_6_label)
            }
            4 -> {
                txtMainTitle.setText(R.string.title_step_7)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_7_label)
            }
            5 -> {
                txtMainTitle.setText(R.string.title_step_8)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_8_label)
            }
            6 -> {
                txtMainTitle.setText(R.string.title_step_13)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_3_label)
            }
            7 -> {
                txtMainTitle.setText(R.string.title_step_14)
                ivMainHeaderImage.setImageResource(R.drawable.img_step_3_label)
            }
        }

        checkImageAvailability()
    }

    private fun checkImageAvailability() {
        if (captureImageFileList[mCounter].isNotEmpty()) {
            imgTakenImage.setImageURI(Uri.fromFile(File(captureImageFileList[mCounter])))
            if (mCurrentPosition == mCounter) {
                cropView.visibility = VISIBLE
            } else {
                imgTakenImage.visibility = VISIBLE
            }
            imgDeleteImage.visibility = VISIBLE
            btnCaptureImage.isEnabled = false
            previewView.visibility = GONE
            btnNext.visibility = VISIBLE
        } else {
            imgTakenImage.visibility = GONE
            cropView.visibility = GONE
            imgDeleteImage.visibility = GONE
            previewView.visibility = VISIBLE
            btnCaptureImage.isEnabled = true
            btnNext.visibility = INVISIBLE
        }
    }

    fun drawRoundRectOnBitmap(
        bitmap: Bitmap
    ): Bitmap? {
        var bitmapConfig = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        val tBitmap = bitmap.copy(bitmapConfig, true)
        return tBitmap
    }

    private fun onCaptureClick() {

        if (checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            checkForPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            val wrapper = ContextWrapper(activity?.applicationContext!!)
            var file = wrapper.getDir("images", Context.MODE_PRIVATE)
            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val pictureFile = "Screening_$timeStamp"
            file = File(file, "$pictureFile.jpg")

            // Save the image in the above file
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            Log.e(TAG,"Path : ${file.absolutePath}")
            captureImage(object : ICaptureCallBack{
                override fun onBegin() {
                    Log.e(TAG, "onBegin");
                }

                override fun onComplete(path: String?) {
                    Log.e(TAG, "onComplete : $path");
                    cropView.visibility = VISIBLE
                    cropView.of(Uri.fromFile(file))
                        .withAspect(1, 1)
                        .initialize(context)
                    captureImageFileList[mCounter]= file.absolutePath
                    checkImageAvailability()
                    LoaderUtil.hideLoading()
                    mViewBinding.cameraViewContainer.visibility = View.INVISIBLE
                }

                override fun onError(error: String?) {
                    Log.e(TAG, "onError : $error");
                    LoaderUtil.hideLoading()
                }

            },file.absolutePath)
        } else
            makePermissionsRequest(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
    }

    private fun onPrevClick() {
        if (mCounter >= 1) {
            mCounter--
            manageTextData()
            btnCaptureImage.isEnabled = captureImageFileList[mCounter].isEmpty()
            imgTakenImage.setImageDrawable(null);
            imgTakenImage.setImageURI(Uri.fromFile(File(captureImageFileList[mCounter])))
            cropView.visibility = GONE
        } else
            btnPrev.visibility = INVISIBLE
    }

    private fun onNextClick() {
        if (mCurrentPosition == mCounter) {
            onCropImage()
        } else {
            onNextWithoutImage()
        }
        btnPrev.visibility = VISIBLE
        cropView.visibility = GONE
    }

    private fun onNextWithoutImage() {
        if (mCounter >= 5) {
            LoaderUtil.showLoading(
                requireActivity(),
                false,
                requireContext().getString(R.string.message_preparing_report)
            )
            requireActivity().stopService(
                Intent(
                    activity,
                    SerialService::class.java
                ))
            (Handler().postDelayed(Runnable { createPdfReport() }, 1000))
        } else {
            mCounter++
            if (mCounter > mCurrentPosition) {
                mCurrentPosition = mCounter
            }
            manageTextData()
            btnCaptureImage.isEnabled = true
            mViewBinding.cameraViewContainer.visibility = View.VISIBLE
        }
    }

    private fun onCropImage() {
        LoaderUtil.showLoading(requireActivity(), false, "Processing Image")
        object : Thread() {
            override fun run() {
                croppedBitmap = drawRoundRectOnBitmap(cropView.output)
                destination = Uri.fromFile(File(captureImageFileList[mCounter]))
                CropUtil.saveOutput(requireContext(), destination, croppedBitmap, 100)
                requireActivity().runOnUiThread(Runnable
                {
                    captureImageFileList[mCounter] = destination!!.path.toString()
                    LoaderUtil.hideLoading()
                    onNextWithoutImage()
                })
            }
        }.start()
    }

    private fun createPdfReport() {
        //LoaderUtil.showLoading(requireActivity(), false, "")
        val maxMemory = (Runtime.getRuntime().maxMemory() / 512).toInt()
        val cacheSize = (maxMemory / 8).toLong()
        val bitmapLruCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(cacheSize)

        for (i in 0 until captureImageFileList.size) {
            if (captureImageFileList[i].isNotEmpty()) {
                val currentBitmap = BitmapFactory.decodeFile(captureImageFileList[i])
                bitmapLruCache.put("$i", currentBitmap)
            }
        }

        val pdfDirectory: File =
            FileUtils().preparePdfDirectory(activity?.applicationContext!!, userId)
        try {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(AppConstant.strHospitalLogoPath)
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            PdfHelper(activity?.applicationContext!!).generatePatientDocument(pdfDirectory, bitmapLruCache,image)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        LoaderUtil.hideLoading()

        goToEndProcedure(pdfDirectory.absolutePath)
    }

    private fun saveFile(encodedData : ByteArray,file : File){
        val bos = BufferedOutputStream(FileOutputStream(file))
        bos.write(encodedData)
        bos.flush()
        bos.close()
    }

    private fun goToEndProcedure(strFilePath: String) {

        //cameraExecutor.shutdown()

        val fragment = EndProcedureFragment()
        val args = Bundle()
        args.putString(AppConstant.INTENT_REPORT_PATH, strFilePath)
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_loader, fragment)?.commit()
    }

    private fun onDeleteClick() {
        activity?.let {
            DialogUtil.showPositiveAndNegativeDialog(
                it, getString(R.string.message_sure_to_retake_image),
                this
            )
        }
    }

    override fun onButtonClick() {
        if (mCounter in 0..5) {
            captureImageFileList[mCounter] = ""
            checkImageAvailability()
            cropView.visibility = INVISIBLE
            mViewBinding.cameraViewContainer.visibility = View.VISIBLE

        }
    }

    /*private fun saveImageToInternalStorage(bytes: ByteArray?) {
        val wrapper = ContextWrapper(activity?.applicationContext!!)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val pictureFile = "Screening_$timeStamp"
        file = File(file, "$pictureFile.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            stream.write(bytes)
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        captureImageFileList.add(mCounter, file.absolutePath)
        checkImageAvailability()
    }*/


    private fun checkForPermission(strPermission: String): Boolean {
        val permission =
            ContextCompat.checkSelfPermission(activity?.applicationContext!!, strPermission)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun makePermissionsRequest(strPermission: Array<String>) {
        ActivityCompat.requestPermissions(
            requireActivity(), strPermission,
            AppConstant.REQUEST_PERMISSIONS_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == AppConstant.REQUEST_PERMISSIONS_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    /*Bluetooth Connection and Service Code*/
    override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
        service = (binder as SerialService.SerialBinder).service
        service!!.attach(this)
        if (initialStart && isResumed) {
            initialStart = false
            requireActivity().runOnUiThread { this.connect() }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    /*
     * SerialListener
     */
    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
    }

    override fun onSerialConnectError(e: java.lang.Exception?) {
        status("connection failed: " + e!!.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray?) {
        if (data != null) {
            receive(data)
        }
    }

    override fun onSerialIoError(e: java.lang.Exception?) {
        status("connection lost: " + e!!.message)
        disconnect()
    }

    var device : BluetoothDevice? = null
    /*
     * Serial + UI
     */
    private fun connect() {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            status("connecting...")
            connected = Connected.Pending
            val socket = SerialSocket(requireActivity().applicationContext, device)
            service!!.connect(socket)
        } catch (e: java.lang.Exception) {
            onSerialConnectError(e)
        }
    }

    private fun disconnect() {
        connected = Connected.False
        service!!.disconnect()
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val msg: String
            val data: ByteArray
            if (hexEnabled) {
                val sb = StringBuilder()
                TextUtil.toHexString(sb, TextUtil.fromHexString(str))
                TextUtil.toHexString(sb, newline.toByteArray())
                msg = sb.toString()
                data = TextUtil.fromHexString(msg)
            } else {
                msg = str
                data = (str + newline).toByteArray()
            }
            val spn = SpannableStringBuilder(
                """
                  $msg
                  
                  """.trimIndent()
            )
            spn.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorSendText)),
                0,
                spn.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //receiveText.append(spn)
            service!!.write(data)
        } catch (e: java.lang.Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(data: ByteArray) {
        if (hexEnabled) {
//            receiveText.append(
//                """
//                ${TextUtil.toHexString(data)}
//
//                """.trimIndent()
//            )
        } else {
            var msg = String(data)
            if (newline == TextUtil.newline_crlf && msg.length > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf)
                Log.e("TAG","msg : ${msg}")
                if(msg.trim() == "@"){
                    btnCaptureImage.performClick()
                } else {
                    Log.e("TAG","::::> calling else")
                }
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg[0] == '\n') {
                    //val edt: Editable = receiveText.getEditableText()
                    //if (edt != null && edt.length > 1) edt.replace(edt.length - 2, edt.length, "")
                }
                pendingNewline = msg[msg.length - 1] == '\r'
            }
            //receiveText.append(TextUtil.toCaretString(msg, newline.length != 0))
        }
    }

    private fun status(str: String) {
        val spn = SpannableStringBuilder(
            """
              $str
              
              """.trimIndent()
        )
        spn.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorStatusText)),
            0,
            spn.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //receiveText.append(spn)
    }

    /*USB Camera Code*/

    override fun getCameraView(): IAspectRatio {
        val aspectRatioTextureView : IAspectRatio  =  AspectRatioTextureView(requireContext())
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        aspectRatioTextureView.setAspectRatio(width,height)

        return aspectRatioTextureView
    }

    override fun initData() {
        super.initData()
        mCameraClient = getCameraClient()!!
        if(mCameraClient.getAllPreviewSizes() != null) {
            for (size in mCameraClient.getAllPreviewSizes()!!) {
                Log.e(TAG, "Size : ${size.width} : ${size.height}")
            }
        }
    }

    override fun getCameraClient(): CameraClient? {
        return CameraClient.newBuilder(requireContext())
            .setEnableGLES(false)   // use opengl render
            .setRawImage(false)     // capture raw or filter image
            .setCameraStrategy(CameraUvcStrategy(requireContext())) // camera type
            .setCameraRequest(getCameraRequest()) // camera configurations
            .setDefaultRotateType(RotateType.ANGLE_270) // default camera rotate angle
            .openDebug(true) // is debug mode
            .build()
    }

    private fun getCameraRequest(): CameraRequest {
        return CameraRequest.Builder()
            .setPreviewWidth(1920)
            .setPreviewHeight(1080)
            .create()
    }

    override fun getCameraViewContainer(): ViewGroup {
        return mViewBinding.cameraViewContainer
    }

    @Suppress("UNREACHABLE_CODE")
    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        mViewBinding = FragmentPatientExaminationBinding.inflate(inflater, container, false)
        root = mViewBinding.root
        return root

        userId = AppConstant.strUserId

        if (userId.isEmpty()) {
            activity?.finish()
        }

        return root
    }

    override fun getGravity(): Int = Gravity.FILL
}
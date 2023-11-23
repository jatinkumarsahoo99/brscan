package vraj.app.cancerscreening.screening.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.INTENT_REPORT_PATH
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PREF_PATIENT_ID
import vraj.app.cancerscreening.screening.data.constant.AppConstant.Companion.PREF_USER_ID
import vraj.app.cancerscreening.screening.data.helper.PreferenceManager
import vraj.app.cancerscreening.screening.data.util.DialogUtil
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EndProcedureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EndProcedureFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var root: View
    private lateinit var btnDownloadReport: Button
    private lateinit var btnShareReport: Button
    private lateinit var btnGoToHome: Button

    private var userId = ""
    private var patientId = ""
    private var strLocalFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.setTitle(R.string.title_end_procedure)
        arguments?.let {
            strLocalFilePath = it.getString(INTENT_REPORT_PATH, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_end_procedure, container, false)
        initUi()
        return root
    }

    private fun initUi() {
        userId = PreferenceManager().getValue(activity?.applicationContext!!, PREF_USER_ID, "")
        patientId = PreferenceManager().getValue(activity?.applicationContext!!, PREF_PATIENT_ID, "")
        if (userId.isEmpty()) {
            activity?.finish()
        }
        val imageView = root.findViewById<ImageView>(R.id.iv_main_report_image)
        btnDownloadReport = root.findViewById(R.id.btn_download_report)
        btnShareReport = root.findViewById(R.id.btn_share_report)
        btnGoToHome = root.findViewById(R.id.btn_back_to_home)

        btnGoToHome.isEnabled = false

        btnDownloadReport.setOnClickListener {
            onDownloadReportClick()
        }

        btnGoToHome.setOnClickListener {
            onEndProcedureClick()
        }

        btnShareReport.setOnClickListener {
            onShareReportClick()
        }

        Glide.with(this)
            .asGif()
            .load(R.drawable.completed_gif) //Your gif resource
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable)
                    {
                        resource.setLoopCount(1)
                    }
                    return false
                }

            }).into(imageView)
    }

    private fun onEndProcedureClick() {
        requireActivity().finishAffinity()
    }

    private fun onDownloadReportClick() {
        if (strLocalFilePath.isEmpty()) {
            showToastMessage(getString(R.string.error_common_message))
        } else {
            val fileReport = File(strLocalFilePath)
            if (fileReport.exists() && fileReport.isFile) {
                uploadReport()
            } else {
                btnGoToHome.isEnabled = false
                showToastMessage(getString(R.string.error_common_message))
            }
        }
    }

    private fun onShareReportClick() {

        try {
            val fileReport = File(strLocalFilePath)

            if(fileReport.exists() && fileReport.isFile)
            {
                /*val fileUri = Uri.fromFile(fileReport)*/
                val fileUri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider", fileReport);
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND

                    putExtra(Intent.EXTRA_TEXT, "Please check the report")
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    type = "application/pdf"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(Intent.createChooser(shareIntent, "Share Br-Scan Report to"))
            }
            else
            {
                showToastMessage(getString(R.string.error_report_not_exists))
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            showToastMessage(getString(R.string.error_common_message))
        }
    }

    private fun uploadReport() {
        btnGoToHome.isEnabled = true
    }

    private fun showToastMessage(strMessage: String) {
        activity?.applicationContext?.let { DialogUtil.showToastMessage(it, strMessage) }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EndProcedureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
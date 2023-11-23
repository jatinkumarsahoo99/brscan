package vraj.app.cancerscreening.screening.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import vraj.app.cancerscreening.screening.data.util.Logger
import vraj.app.cancerscreening.screening.ui.clinic_fragments.HospitalPatientExaminationFragment
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.formatter.PushDownAnim


/**
 * A simple [Fragment] subclass.
 * Use the [StepViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StepViewFragment : Fragment() {

    private lateinit var root: View
    private lateinit var ivHeaderImage: ImageView
    private lateinit var ivMainImage: ImageView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private var deviceAddress: String? = null

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceAddress = requireArguments().getString("device")
        /*activity?.setTitle(R.string.title_steps)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Logger.showErrorLog("Jai Shree Ram")
        root = inflater.inflate(R.layout.fragment_step_view, container, false)
        initUi()
        return root
    }

    private fun initUi() {
        ivHeaderImage = root.findViewById(R.id.iv_main_header_image)
        ivMainImage = root.findViewById(R.id.iv_main_image)
        tvHeaderTitle = root.findViewById(R.id.tv_main_header_label)
        btnNext = root.findViewById(R.id.btn_next_step)
        btnPrev = root.findViewById(R.id.btn_prev_step)

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

        btnNext.setOnClickListener { onNextButtonClick() }
        btnPrev.setOnClickListener { onPrevButtonClick() }

        manageViews()
    }

    private fun onNextButtonClick() {
        if (counter == 0) {
            counter = 1
            manageViews()
        } else {
            goToExaminationScreen()
        }
    }

    private fun onPrevButtonClick() {
        if (counter > 0) {
            counter--
            manageViews()
        }
    }

    private fun manageViews() {
        if (counter == 0) {
            btnPrev.visibility = View.GONE
            ivHeaderImage.setImageResource(R.drawable.img_step_1_label)
            ivMainImage.setImageResource(R.drawable.img_step_1)
            tvHeaderTitle.setText(R.string.title_step_1)
        } else {
            btnPrev.visibility = View.VISIBLE
            ivHeaderImage.setImageResource(R.drawable.img_step_2_label)
            ivMainImage.setImageResource(R.drawable.img_step_2)
            tvHeaderTitle.setText(R.string.title_step_2)
        }
    }

    private fun goToExaminationScreen() {
        val fragment: Fragment = HospitalPatientExaminationFragment()
        val args = Bundle()
        args.putString("device", deviceAddress)
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_loader, fragment)?.commit()
    }
}
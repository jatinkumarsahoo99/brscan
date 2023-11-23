package vraj.app.cancerscreening.screening.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import vraj.app.cancerscreening.screening.bluetooth.DialogBluetoothSelectFragment
import vraj.app.cancer.screening.ui.fragments.BaseFragment
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.ui.activities.BaseActivity
import vraj.app.cancerscreening.screening.data.constant.AppConstant


@SuppressLint("MissingPermission")
class BluetoothFragment : BaseFragment(),
    DialogBluetoothSelectFragment.OnDialogBluetoothSelectFragmentListener {

    private lateinit var rootView: View
    private lateinit var dialogBluetoothSelectFragment: DialogBluetoothSelectFragment

    private lateinit var img_bluetooth : ImageView
    private lateinit var img_home : ImageView
    private lateinit var txt_home_text : TextView
    private lateinit var btn_connect : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false)
        img_bluetooth = rootView.findViewById(R.id.img_bluetooth)
        img_home = rootView.findViewById(R.id.img_home)
        txt_home_text = rootView.findViewById(R.id.txt_home_text)
        btn_connect = rootView.findViewById(R.id.btn_connect)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mActivity: BaseActivity = requireActivity() as BaseActivity

        dialogBluetoothSelectFragment = DialogBluetoothSelectFragment(this)
        Glide.with(this).asGif().load(R.drawable.animation_500_l8nhabiu)
            .into(img_bluetooth)

        img_home.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                returnHome(mActivity)
            }
        })

        txt_home_text.setOnClickListener { returnHome(mActivity) }

        btn_connect.setOnClickListener {
            if (hasPermission(
                    mActivity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) && hasPermission(mActivity, Manifest.permission.BLUETOOTH_SCAN)
            ) {
                dialogBluetoothSelectFragment.show(
                    requireActivity().supportFragmentManager,
                    "DialogFragment"
                )
            } else {
                Log.e("TAG", "Request Permission")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(
                        mActivity,
                        AppConstant.REQUEST_BLUETOOTH_PERMISSION.toTypedArray(),
                        AppConstant.REQUEST_PERMISSIONS_CODE
                    )
                } else {
                    dialogBluetoothSelectFragment.show(
                        requireActivity().supportFragmentManager,
                        "DialogFragment"
                    )
                }
            }
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED;
    }

    override fun onDestroy() {
        super.onDestroy()
       /* if (ContextUtils.getAppType()) {
            val mActivity: HomeHospitalActivity = requireActivity() as HomeHospitalActivity
            if (mActivity.supportActionBar != null) {
                mActivity.supportActionBar!!.show()
            }
        }*/
    }

    private fun returnHome(mActivity: BaseActivity) {
        /*mActivity.replaceFragmenty(
            fragment = HomeFragment(),
            allowStateLoss = true,
            containerViewId = R.id.nav_host_fragment
        )
        mActivity.title = getString(R.string.menu_home)*/
    }

    override fun onHomeClick() {
        dialogBluetoothSelectFragment.dismiss()
        /*if (ContextUtils.getAppType()) {
            returnHome(requireActivity() as HomeHospitalActivity)
        } else {
            returnHome(requireActivity() as HomeActivity)
        }*/
    }

    override fun onGoToSelfExamination(address: String) {
        val fragment = StepViewFragment()
        val args = Bundle()
        args.putString("device", address)
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_loader, fragment)?.commit()
        dialogBluetoothSelectFragment.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == AppConstant.REQUEST_PERMISSIONS_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                btn_connect.performClick()
            }
        }
    }
}
package vraj.app.cancerscreening.screening.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vraj.app.cancerscreening.screening.data.adapter.BluetoothRecyclerViewAdapter
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.data.util.LoaderUtil
import vraj.app.cancerscreening.screening.R


class DialogBluetoothSelectFragment (listener : OnDialogBluetoothSelectFragmentListener): DialogFragment(),
    ListInteractionListener<BluetoothDevice>,
    BluetoothRecyclerViewAdapter.OnBluetoothRecyclerViewAdapterListener {

    private lateinit var mAdapter: BluetoothRecyclerViewAdapter
    private var mDeviceList = mutableListOf<BluetoothDevice>()
    private lateinit var rootView: View
    private lateinit var bluetoothController: BluetoothController
    private var selectedDevice: BluetoothDevice? = null
    private val TAG = "BT"
    private var mListener : OnDialogBluetoothSelectFragmentListener = listener
    private lateinit var rv_bluetooth_pair_list : RecyclerView
    private lateinit var img_home : ImageView
    private lateinit var img_back : ImageView
    private lateinit var txt_home_text : TextView
    private lateinit var tv_no_device_found : TextView
    private lateinit var grpProgressBar : androidx.constraintlayout.widget.Group



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.dialog_select_bluetooth, container, false)
        rv_bluetooth_pair_list = rootView.findViewById(R.id.rv_bluetooth_pair_list)
        img_home = rootView.findViewById(R.id.img_home) as ImageView
        img_back = rootView.findViewById(R.id.img_back) as ImageView
        txt_home_text = rootView.findViewById(R.id.txt_home_text)
        tv_no_device_found = rootView.findViewById(R.id.tv_no_device_found)
        grpProgressBar = rootView.findViewById(R.id.grpProgressBar)
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.removeListener()
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*setup Bluetooth Controller*/
        mAdapter = BluetoothRecyclerViewAdapter(mDeviceList,requireContext(),this,this)
        bluetoothController =
            BluetoothController(requireActivity(), BluetoothAdapter.getDefaultAdapter(), mAdapter)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_bluetooth_pair_list.layoutManager = layoutManager
        rv_bluetooth_pair_list.adapter = mAdapter
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            layoutManager.orientation
        )
        rv_bluetooth_pair_list.addItemDecoration(dividerItemDecoration)
        /*setup Bluetooth Controller*/

        img_home.setOnClickListener { mListener.onHomeClick() }
        img_back.setOnClickListener { dismiss() }

        txt_home_text.setOnClickListener { mListener.onHomeClick() }
        grpProgressBar.visibility = View.VISIBLE
        startBluetoothConnect()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun startBluetoothConnect() {
        if (!bluetoothController.isBluetoothEnabled) {
            bluetoothController.turnOnBluetoothAndScheduleDiscovery()
        } else {
            //Prevents the user from spamming the button and thus glitching the UI.
            val pairedDevices = bluetoothController.getPairedDevices()
            /*if (pairedDevices.isNotEmpty()) {*/
            connectPairedDevices(pairedDevices)
            /*} else {*/
            startNewBluetoothDeviceDiscovery()
            /*}*/
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectPairedDevices(pairedDevices: Set<BluetoothDevice>) {
        val devices: Array<BluetoothDevice> = pairedDevices.toTypedArray()
        //val deviceNotFound = 0
        for (device in devices) {
            if(!mDeviceList.contains(device) && !device.name.isNullOrEmpty() && device.name.contains(
                    AppConstant.BLUETOOTHDEVICENAME)) {
                Log.e("TAG","Adding Device");
                mDeviceList.add(device)
            }
            /*if (device.name.contains(AppConstant.BLUETOOTHDEVICENAME)) {
                Log.e(TAG, "device Found : ${device.name} : device UUID : ${device.bondState}")
                if (device.bondState == 12) {
                    endLoadingWithDialog(false, device)
                } else if (bluetoothController.pair(device)) {
                    Log.e(TAG, "device Connected")
                    endLoadingWithDialog(false, device)
                    deviceNotFound++
                    break
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("We are unable to find your device please switch on the device and click yes.")
                        .setCancelable(false)
                        .setPositiveButton("Yes"
                        ) { _, _ -> connectPairedDevices(pairedDevices) }
                        .setNegativeButton("No"
                        ) { dialog, _ -> dialog.cancel() }
                    val alert: AlertDialog = builder.create()
                    alert.show()
                    Log.e(TAG, "device Found But Not Connected")
                }
            }*/
            Log.e(TAG, "device : ${device.name}")
        }
        /*if (deviceNotFound == 0) {
            startNewBluetoothDeviceDiscovery()
        }*/
    }

    private fun startNewBluetoothDeviceDiscovery() {
        if (!bluetoothController.isDiscovering) {
            bluetoothController.startDiscovery()
        } else {
            bluetoothController.cancelDiscovery()
        }
    }

    override fun onItemClick(device: BluetoothDevice?) {
        Log.d("BT", "Item clicked : " + BluetoothController.deviceToString(device!!))
        if(BluetoothController.deviceToString(device!!).contains(AppConstant.BLUETOOTHDEVICENAME)){
            if (bluetoothController.isAlreadyPaired(device)) {
                Log.d(TAG, "Device already paired!")
                Toast.makeText(requireContext(), "Already Paired", Toast.LENGTH_SHORT).show()
                //loadTerminalFragment(device.address)
                mListener.onGoToSelfExamination(device.address)
            } else {
                Log.d(TAG, "Device not paired. Pairing.")
                val outcome: Boolean = bluetoothController.pair(device)
                // Prints a message to the user.
                val deviceName = BluetoothController.getDeviceName(device)
                selectedDevice = device
                if (outcome) {
                    // The pairing has started, shows a progress dialog.
                    Log.d(TAG, "Showing pairing dialog")
                    LoaderUtil.showLoading(requireContext(),false,"Pairing with device $deviceName...")
                } else {
                    Log.d(TAG, "Error while pairing with device $deviceName!")
                    Toast.makeText(
                        requireContext(),
                        "Error while pairing with device $deviceName!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun startLoading() {
        //LoaderUtil.showLoading(requireContext(),false,"getting bluetooth devices")
    }

    override fun endLoading(partialResults: Boolean) {
        //LoaderUtil.hideLoading()
    }

    @Suppress("DEPRECATION", "KotlinConstantConditions")
    override fun endLoadingWithDialog(error: Boolean, element: BluetoothDevice?) {
        Log.e("BT","endLoadingWithDialog")
        LoaderUtil.hideLoading()
        grpProgressBar.visibility = View.GONE
        if(!error) {
            LoaderUtil.showLoading(requireContext(), true, "Pairing Please Wait...")
            (Handler()).postDelayed({
                if(bluetoothController.isPairingInProgress){
                    LoaderUtil.hideLoading()
                    endLoadingWithDialog(error,element)
                } else {
                    LoaderUtil.hideLoading()
                    //loadTerminalFragment(selectedDevice!!.address)
                    //goToSelfExamination(element!!.address)
                    mListener.onGoToSelfExamination(element!!.address)
                }
            }, 5000)
        }
    }

    /*private fun goToSelfExamination(address : String){
        val fragment = StepViewFragment()
        val args = Bundle()
        args.putString("device", address)
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.nav_host_fragment, fragment)?.commit()
        dismiss()
    }*/

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.cancelDiscovery()
    }

    interface OnDialogBluetoothSelectFragmentListener{
        fun onHomeClick()
        fun onGoToSelfExamination(address: String)
    }

    override fun onOiscoveryEnd() {
        try {
            if (mAdapter.getDeviceList().size == 0) {
                tv_no_device_found.visibility = View.VISIBLE
                rv_bluetooth_pair_list.visibility = View.INVISIBLE
            } else {
                tv_no_device_found.visibility = View.GONE
                rv_bluetooth_pair_list.visibility = View.VISIBLE
            }
            grpProgressBar.visibility = View.INVISIBLE
        }catch (ex : java.lang.Exception){
            ex.printStackTrace()
        }
    }
}
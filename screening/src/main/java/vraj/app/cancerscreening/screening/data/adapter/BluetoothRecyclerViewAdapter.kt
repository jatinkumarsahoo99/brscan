package vraj.app.cancerscreening.screening.data.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vraj.app.cancerscreening.screening.bluetooth.BluetoothController
import vraj.app.cancerscreening.screening.data.constant.AppConstant
import vraj.app.cancerscreening.screening.data.util.LoaderUtil
import vraj.app.cancerscreening.screening.R
import vraj.app.cancerscreening.screening.bluetooth.BluetoothDiscoveryDeviceListener
import vraj.app.cancerscreening.screening.bluetooth.ListInteractionListener


class BluetoothRecyclerViewAdapter(
    deviceList: MutableList<BluetoothDevice>,
    context: Context,
    listener: ListInteractionListener<BluetoothDevice>,
    adapterLisetener : OnBluetoothRecyclerViewAdapterListener
) :
    RecyclerView.Adapter<BluetoothRecyclerViewAdapter.ViewHolder>(),
    BluetoothDiscoveryDeviceListener {
    private lateinit var layoutInflater: LayoutInflater
    private val mContext: Context
    private val mDeviceList: MutableList<BluetoothDevice>
    private var mListener: ListInteractionListener<BluetoothDevice>? = null
    private var bluetooth: BluetoothController? = null
    private val mAdapterListener : OnBluetoothRecyclerViewAdapterListener

    init {
        mContext = context
        mDeviceList = deviceList
        mListener = listener
        mAdapterListener = adapterLisetener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        layoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ViewHolder(layoutInflater.inflate(R.layout.item_bluetooth, parent, false))
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.root.tag = position
        holder.myTextView.text = "deviceName : ${mDeviceList[position].name}"
        Log.e("TAG","mDeviceList[position].name : ${mDeviceList[position].name}")
        /*if (mDeviceList[position].name.contains(AppConstant.BLUETOOTHDEVICENAME)) {*/
            holder.img_prefered.visibility = View.VISIBLE
        /*} else {
            holder.img_prefered.visibility = View.INVISIBLE
        }*/
        holder.root.setOnClickListener { p0 ->
            Log.e("BT", "Position : ${p0!!.tag}")
            if (mListener != null) {
                mListener!!.onItemClick(mDeviceList[p0.tag.toString().toInt()])
            }
        }
    }

    override fun getItemCount(): Int {
        return mDeviceList.size
    }


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var myTextView: TextView
        var root: ConstraintLayout
        var img_prefered: AppCompatImageView

        init {
            myTextView = itemView.findViewById(R.id.textView)
            root = itemView.findViewById(R.id.root)
            img_prefered = itemView.findViewById(R.id.img_prefered)
        }
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    override fun onDeviceDiscovered(device: BluetoothDevice?) {
        Log.e("BT", "onDeviceDiscovered : ${device!!.name}")
        if (mListener != null) {
            mListener!!.endLoading(true)
        }
        if (!mDeviceList.contains(device)) {
            /*if(!device.name.isNullOrBlank() && device.name.contains(AppConstant.BLUETOOTHDEVICENAME)){
                if(bluetooth!!.pair(device)){
                    if(mListener != null) {
                        mListener!!.endLoadingWithDialog(false, device)
                    }
                }
            }*/
            if(!device.name.isNullOrBlank() && device.name.contains(AppConstant.BLUETOOTHDEVICENAME)) {
                mDeviceList.add(device)
                notifyDataSetChanged()
            }
        }
    }

    override fun onDeviceDiscoveryStarted() {
        Log.e("BT", "onDeviceDiscoveryStarted")
        if (mListener != null) {
            mListener!!.startLoading()
        }
    }

    override fun setBluetoothController(controller: BluetoothController?) {
        bluetooth = controller
    }

    override fun onDeviceDiscoveryEnd() {
        Log.e("BT", "onDeviceDiscoveryEnd")
        if (mListener != null) {
            mListener!!.endLoading(false)
        }
        if(mAdapterListener != null){
            mAdapterListener.onOiscoveryEnd()
        }
    }

    override fun onBluetoothStatusChanged() {
        Log.e("BT", "onBluetoothStatusChanged")
        bluetooth!!.onBluetoothStatusChanged();
    }

    override fun onBluetoothTurningOn() {
        Log.e("BT", "onBluetoothTurningOn")
        LoaderUtil.showLoading(mContext, true, "Starting Bluetooth")
    }

    override fun onDevicePairingEnded() {
        Log.e("BT", "onDevicePairingEnded")
        if (bluetooth!!.isPairingInProgress) {
            val device: BluetoothDevice = bluetooth!!.boundingDevice!!
            when (bluetooth!!.pairingDeviceStatus) {
                BluetoothDevice.BOND_BONDING -> {}
                BluetoothDevice.BOND_BONDED -> {
                    // Successfully paired.
                    if (mListener != null) {
                        mListener!!.endLoadingWithDialog(false, device)
                    }
                    // Updates the icon for this element.
                    notifyDataSetChanged()
                }
                BluetoothDevice.BOND_NONE ->                     // Failed pairing.
                    if (mListener != null) {
                        mListener!!.endLoadingWithDialog(true, device)
                    }
            }
        }
    }

    fun getDeviceList() : MutableList<BluetoothDevice>{
        return mDeviceList;
    }

    public fun removeListener() {
        mListener = null
    }

    interface OnBluetoothRecyclerViewAdapterListener{
        fun onOiscoveryEnd()
    }

}
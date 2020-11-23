package com.xixi.sdk.android.device.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable
import com.xixi.sdk.controller.LLNotifier

abstract class LLUsbDevice protected constructor() : LLNotifier<LLUSBEvent?>() {
    //	private final static String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    //	private final static String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    lateinit var ids: Array<IntArray>
    protected abstract fun onUsbMsgHandle(intent: Intent?)
    protected abstract val pid_Vids: Array<IntArray>
    protected fun onAttach(i: Intent?) {}
    protected fun onDetach(i: Intent?) {}
    protected abstract val intentFilter: IntentFilter

    private interface ICallback {
        fun onHandle(i: Intent?)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val isAttachedMsg = UsbManager.ACTION_USB_DEVICE_ATTACHED == action
            if (isAttachedMsg || UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val callback: ICallback = object : ICallback {
                    override fun onHandle(i: Intent?) {
                        if (isAttachedMsg) {
                            onAttach(intent)
                        } else {
                            onDetach(intent)
                        }
                    }
                }
                val device =
                    intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice
                if (device != null) {
                    for (node_group in ids) {
                        if (node_group[0] == device.vendorId && node_group[1] == device.productId) {
                            callback.onHandle(intent)
                            break
                        }
                    }
                }
            } else {
                onUsbMsgHandle(intent)
            }
        }
    }

    init {
        ids = pid_Vids
        val intent_filter = intentFilter
        intent_filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        intent_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(broadcastReceiver, intent_filter)
    }
}
package com.example.bantaybahay.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.R

class DeviceListAdapter(
    private val onDeviceClick: (Device) -> Unit,
    private val onDeviceLongClick: (Device) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    private var devices: List<Device> = emptyList()

    fun updateDevices(newDevices: List<Device>) {
        this.devices = newDevices
        notifyDataSetChanged()
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvDeviceName)
        val statusTextView: TextView = itemView.findViewById(R.id.tvDeviceStatus)
        val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.nameTextView.text = device.name
        holder.statusTextView.text = device.status.replaceFirstChar { it.uppercase() }

        val color = if (device.status.equals("online", ignoreCase = true)) {
            android.graphics.Color.parseColor("#4CAF50")
        } else {
            android.graphics.Color.GRAY
        }
        holder.statusIndicator.background.setTint(color)

        holder.itemView.setOnClickListener { onDeviceClick(device) }
        holder.itemView.setOnLongClickListener {
            onDeviceLongClick(device)
            true
        }
    }

    override fun getItemCount() = devices.size
}
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
        
        // Heartbeat Logic: Check if device has been seen in last 30 seconds
        val currentTime = System.currentTimeMillis() / 1000 // Convert to seconds
        val isOffline = (currentTime - device.lastSeen) > 30

        if (isOffline) {
            holder.statusTextView.text = "Offline"
            holder.statusIndicator.background.setTint(android.graphics.Color.RED)
        } else {
            holder.statusTextView.text = "Online"
            holder.statusIndicator.background.setTint(android.graphics.Color.parseColor("#4CAF50"))
        }

        holder.itemView.setOnClickListener { onDeviceClick(device) }
        holder.itemView.setOnLongClickListener {
            onDeviceLongClick(device)
            true
        }
    }

    override fun getItemCount() = devices.size
}
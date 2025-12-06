package com.example.bantaybahay.AllEvents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.R

class AllEventsAdapter : RecyclerView.Adapter<AllEventsAdapter.ViewHolder>() {

    // Triple: Timestamp, Message, DeviceName
    private val items = mutableListOf<Triple<String, String, String>>()

    fun setEvents(events: List<Triple<String, String, String>>) {
        items.clear()
        items.addAll(events)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        val tvDeviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (time, message, deviceName) = items[position]

        // Split timestamp into date + time parts
        val parts = time.split("_")
        val date = parts.getOrNull(0) ?: ""          // YYYY-MM-DD
        val rawTime = parts.getOrNull(1) ?: ""       // HH-MM-SS

        // Convert HH-MM-SS to HH:MM:SS
        val formattedTime = rawTime.replace("-", ":")

        holder.tvTime.text = "$date $formattedTime"
        holder.tvDetails.text = message
        holder.tvDeviceName.text = deviceName
    }

    override fun getItemCount() = items.size
}

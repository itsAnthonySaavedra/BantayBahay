package com.example.bantaybahay.AllEvents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.R

// This data class will hold the event and the name of the device it came from
data class TitledEvent(
    val deviceName: String,
    val eventType: String,
    val timestamp: String
)

class AllEventsAdapter : RecyclerView.Adapter<AllEventsAdapter.EventViewHolder>() {

    private var events: List<TitledEvent> = emptyList()

    fun updateEvents(newEvents: List<TitledEvent>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        // We now combine the device name and the event type for a clearer log
        holder.eventTitle.text = "${event.deviceName}: ${event.eventType}"
        holder.eventTime.text = event.timestamp

        val color = when {
            event.eventType.contains("Closed", ignoreCase = true) -> android.graphics.Color.parseColor("#4CAF50") // Green
            event.eventType.contains("Opened", ignoreCase = true) -> android.graphics.Color.parseColor("#FFD600") // Yellow
            event.eventType.contains("Detected", ignoreCase = true) -> android.graphics.Color.parseColor("#2196F3") // Blue
            else -> android.graphics.Color.GRAY
        }
        holder.statusIndicator.setBackgroundColor(color)
    }

    override fun getItemCount() = events.size
}
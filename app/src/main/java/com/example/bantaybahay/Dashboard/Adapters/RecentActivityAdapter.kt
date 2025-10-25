package com.example.bantaybahay.Dashboard.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Dashboard.Event // Correct import
import com.example.bantaybahay.R

class RecentActivityAdapter(private val events: List<Event>) : RecyclerView.Adapter<RecentActivityAdapter.EventViewHolder>() {
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
        holder.eventTitle.text = event.type
        holder.eventTime.text = event.timestamp

        val color = when {
            event.type.contains("Closed", ignoreCase = true) -> android.graphics.Color.parseColor("#4CAF50") // Green
            event.type.contains("Opened", ignoreCase = true) -> android.graphics.Color.parseColor("#FFD600") // Yellow
            event.type.contains("Detected", ignoreCase = true) -> android.graphics.Color.parseColor("#2196F3") // Blue
            else -> android.graphics.Color.GRAY
        }
        holder.statusIndicator.setBackgroundColor(color)
    }

    override fun getItemCount(): Int = events.size
}
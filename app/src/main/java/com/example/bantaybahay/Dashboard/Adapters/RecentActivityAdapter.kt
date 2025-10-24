package com.example.bantaybahay.Dashboard.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.Dashboard.Event
import com.example.bantaybahay.Dashboard.Events.DashboardEvent
import com.example.bantaybahay.R

class RecentActivityAdapter(private val events: List<DashboardEvent>) : RecyclerView.Adapter<RecentActivityAdapter.EventViewHolder>() {
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
        holder.eventTitle.text = event.title
        holder.eventTime.text = event.time

        val color = when (event.type.lowercase()) {
            "closed" -> android.graphics.Color.parseColor("#4CAF50")
            "opened" -> android.graphics.Color.parseColor("#FFD600")
            "detected" -> android.graphics.Color.parseColor("#2196F3")
            else -> android.graphics.Color.GRAY
        }
        holder.statusIndicator.setBackgroundColor(color)
    }

    override fun getItemCount(): Int = events.size
}


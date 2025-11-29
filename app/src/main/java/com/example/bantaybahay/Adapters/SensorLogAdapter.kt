package com.example.bantaybahay.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bantaybahay.R

class SensorLogAdapter : RecyclerView.Adapter<SensorLogAdapter.ViewHolder>() {

    private val logs = mutableListOf<Pair<String, String>>()

    fun setLogs(list: List<Pair<String, String>>) {
        logs.clear()
        logs.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTime: TextView = view.findViewById(R.id.textTime)
        val textState: TextView = view.findViewById(R.id.textState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (time, state) = logs[position]

        val parts = time.split("_")
        val date = parts[0]              // YYYY-MM-DD
        val timePart = parts[1].replace("-", ":") // HH:MM:SS

        holder.textTime.text = "$date $timePart"
        holder.textState.text = state
    }

    override fun getItemCount(): Int = logs.size
}
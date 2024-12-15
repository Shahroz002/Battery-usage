//
//package com.example.batteryusage
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//data class BatteryUsageData(
//    val packageName: String,
//    val foregroundTime: Long,
//    val batteryConsumed: Double
//)
//
//class BatteryUsageAdapter(private val batteryUsageDataList: List<BatteryUsageData>) :
//    RecyclerView.Adapter<BatteryUsageAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_battery_usage, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val data = batteryUsageDataList[position]
//        holder.packageName.text = data.packageName
//        holder.foregroundTime.text = formatTime(data.foregroundTime)
//        holder.batteryConsumed.text = "${String.format("%.2f", data.batteryConsumed)} mAh"
//    }
//
//    override fun getItemCount(): Int = batteryUsageDataList.size
//
//    private fun formatTime(milliseconds: Long): String {
//        val seconds = milliseconds / 1000
//        val minutes = seconds / 60
//        val hours = minutes / 60
//        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val packageName: TextView = itemView.findViewById(R.id.packageName)
//        val foregroundTime: TextView = itemView.findViewById(R.id.foregroundTime)
//        val batteryConsumed: TextView = itemView.findViewById(R.id.batteryConsumed)
//    }
//}

package com.example.batteryusage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class BatteryUsageData(
    val packageName: String,
    val foregroundTime: Long,
    val batteryConsumed: Double
)

class BatteryUsageAdapter(private val batteryUsageDataList: List<BatteryUsageData>) :
    RecyclerView.Adapter<BatteryUsageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_battery_usage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = batteryUsageDataList[position]
        holder.packageName.text = data.packageName
        holder.foregroundTime.text = formatTime(data.foregroundTime)
        holder.batteryConsumed.text = "${String.format("%.2f", data.batteryConsumed)} mAh"
    }

    override fun getItemCount(): Int = batteryUsageDataList.size

    private fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageName: TextView = itemView.findViewById(R.id.packageName)
        val foregroundTime: TextView = itemView.findViewById(R.id.foregroundTime)
        val batteryConsumed: TextView = itemView.findViewById(R.id.batteryConsumed)
    }
}

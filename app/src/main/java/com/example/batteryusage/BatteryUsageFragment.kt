//package com.example.batteryusage
//
//import android.app.usage.UsageStatsManager
//import android.content.Context
//import android.content.Intent
//import android.os.BatteryManager
//import android.os.Bundle
//import android.provider.Settings
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//class BatteryUsageFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_battery_usage, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Add a divider between items
//        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)
//        if (drawable != null) {
//            itemDecoration.setDrawable(drawable)
//        }
//        recyclerView.addItemDecoration(itemDecoration)
//
//        // Check for usage access permission
//        if (!hasUsageAccessPermission()) {
//            requestUsageAccessPermission()
//        } else {
//            val batteryUsageStats = getAppBatteryUsageStats()
//            val adapter = BatteryUsageAdapter(batteryUsageStats)
//            recyclerView.adapter = adapter
//        }
//    }
//
//    private fun hasUsageAccessPermission(): Boolean {
//        val usageStatsManager =
//            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val stats = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY,
//            System.currentTimeMillis() - 1000 * 60 * 60 * 24,
//            System.currentTimeMillis()
//        )
//        return stats != null && stats.isNotEmpty()
//    }
//
//    private fun requestUsageAccessPermission() {
//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        startActivity(intent)
//    }
//
//    private fun getAppBatteryUsageStats(): List<BatteryUsageData> {
//        val usageStatsManager =
//            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val packageManager = requireContext().packageManager
//
//        val endTime = System.currentTimeMillis()
//        val startTime = endTime - java.util.concurrent.TimeUnit.HOURS.toMillis(24)
//
//        val usageStatsList = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY,
//            startTime,
//            endTime
//        )
//
//        val totalBatteryCapacity = getBatteryCapacity()
//        val batteryUsageDataList = mutableListOf<BatteryUsageData>()
//
//        if (usageStatsList.isNullOrEmpty()) {
//            Log.e("BatteryUsage", "No usage stats available. Check permissions.")
//            return batteryUsageDataList
//        }
//
//        usageStatsList.forEach { usageStats ->
//            if (usageStats.totalTimeInForeground > 0) {
//                val packageName = usageStats.packageName
//                val appName = try {
//                    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
//                    packageManager.getApplicationLabel(applicationInfo).toString()
//                } catch (e: Exception) {
//                    Log.e("BatteryUsage", "Error fetching app name: $packageName", e)
//                    packageName
//                }
//
//                val batteryUsagePercent =
//                    usageStats.totalTimeInForeground.toFloat() / (24 * 60 * 60 * 1000) * 100
//                val estimatedBatteryConsumption = (totalBatteryCapacity * batteryUsagePercent) / 100
//
//                batteryUsageDataList.add(
//                    BatteryUsageData(
//                        packageName = appName,
//                        foregroundTime = usageStats.totalTimeInForeground,
//                        batteryConsumed = estimatedBatteryConsumption
//                    )
//                )
//            }
//        }
//
//        Log.d("BatteryUsage", "Fetched ${batteryUsageDataList.size} usage stats.")
//        return batteryUsageDataList
//    }
//
//    private fun getBatteryCapacity(): Double {
//        return try {
//            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
//            val powerProfile = powerProfileClass.getConstructor(Context::class.java)
//                .newInstance(requireContext())
//            val batteryCapacityMethod = powerProfileClass.getMethod("getBatteryCapacity")
//            batteryCapacityMethod.invoke(powerProfile) as Double
//        } catch (e: Exception) {
//            e.printStackTrace()
//            4000.0 // Default to 4000 mAh if battery capacity can't be fetched
//        }
//    }
//}


package com.example.batteryusage

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BatteryUsageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_battery_usage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add a divider for better visual separation
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)?.let {
            divider.setDrawable(it)
        }
        recyclerView.addItemDecoration(divider)

        // Check if permission is granted
        if (!hasUsageAccessPermission()) {
            showPermissionDialog()
        } else {
            fetchAndDisplayBatteryUsage()
        }
    }

    private fun hasUsageAccessPermission(): Boolean {
        val usageStatsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 1000 * 60 * 60 * 24, // Last 24 hours
            System.currentTimeMillis()
        )
        return stats != null && stats.isNotEmpty()
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("This app needs usage access permission to calculate battery usage for apps. Please grant the permission in the next screen.")
            .setPositiveButton("Grant Permission") { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(requireContext(), "Permission is required to proceed", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun fetchAndDisplayBatteryUsage() {
        val batteryUsageStats = getAppBatteryUsageStats()
        val adapter = BatteryUsageAdapter(batteryUsageStats)
        recyclerView.adapter = adapter
    }

    private fun getAppBatteryUsageStats(): List<BatteryUsageData> {
        val usageStatsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val packageManager = requireContext().packageManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - java.util.concurrent.TimeUnit.HOURS.toMillis(24)

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        val totalBatteryCapacity = getBatteryCapacity()
        val batteryUsageDataList = mutableListOf<BatteryUsageData>()

        if (usageStatsList.isNullOrEmpty()) {
            Log.e("BatteryUsage", "No usage stats available. Check permissions.")
            return batteryUsageDataList
        }

        usageStatsList.forEach { usageStats ->
            if (usageStats.totalTimeInForeground > 0) {
                val packageName = usageStats.packageName
                val appName = try {
                    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                    packageManager.getApplicationLabel(applicationInfo).toString()
                } catch (e: Exception) {
                    Log.e("BatteryUsage", "Error fetching app name for $packageName", e)
                    packageName
                }

                val batteryUsagePercent =
                    usageStats.totalTimeInForeground.toFloat() / (24 * 60 * 60 * 1000) * 100
                val estimatedBatteryConsumption = (totalBatteryCapacity * batteryUsagePercent) / 100

                batteryUsageDataList.add(
                    BatteryUsageData(
                        packageName = appName,
                        foregroundTime = usageStats.totalTimeInForeground,
                        batteryConsumed = estimatedBatteryConsumption
                    )
                )
            }
        }

        return batteryUsageDataList
    }

    private fun getBatteryCapacity(): Double {
        return try {
            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
            val powerProfile = powerProfileClass.getConstructor(Context::class.java)
                .newInstance(requireContext())
            val batteryCapacityMethod = powerProfileClass.getMethod("getBatteryCapacity")
            batteryCapacityMethod.invoke(powerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
            4000.0 // Default value
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageAccessPermission()) {
            fetchAndDisplayBatteryUsage()
        }
    }
}

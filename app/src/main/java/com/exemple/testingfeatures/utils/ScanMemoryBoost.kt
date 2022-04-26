package com.exemple.testingfeatures.utils

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import java.io.File
import java.util.*

class ScanMemoryBoost(ctx: Context) {
    private val activityManager: ActivityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val packageManager: PackageManager = ctx.packageManager
    private val context: Context = ctx
    var deletedSize: Long = 0
    private val packageList: MutableList<ProcessInfo> = ArrayList()
    var TAG = this.javaClass.name
    fun startScan(): List<ProcessInfo> {
        createListOfRunningApps()
        return packageList
    }

    fun calculatePercUsage() {
        if (packageList.size > 0) {
            var total: Long = 0
            for (pi in packageList) {
                total += pi.appTotalUsageTime
            }
            for (pi in packageList) {
                pi.percUsage = (100 * pi.appTotalUsageTime / total).toInt()
            }
        }
    }

    fun clearCache() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val methods = packageManager.javaClass.declaredMethods
            for (m in methods) {
                if (m.name == "freeStorage") {
                    try {
                        val desiredFreeStorage = Long.MAX_VALUE
                        m.invoke(packageManager, desiredFreeStorage, null)
                    } catch (e: Exception) {
                    }
                    break
                }
            }
        }
    }

    fun cleanDownloadFolder() {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        try {
            val deletedSize = deleteRecursive(root)
        } catch (e: Exception) {
            Log.wtf(TAG, "Clean folder error " + e.message)
        }
    }

    fun deleteRecursive(dir: File): Long {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.path)
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val temp = File(dir, children[i])
                deletedSize += if (temp.isDirectory) {
                    Log.d("DeleteRecursive", "Recursive Call" + temp.path)
                    deleteRecursive(temp)
                } else {
                    deleteFile(temp)
                }
            }
        } else {
            deletedSize += deleteFile(dir)
        }
        return deletedSize
    }

    private fun deleteFile(file: File): Long {
        try {
            val fileSize = file.length()
            if (file.delete()) {
                Log.d("DeleteRecursive", "Delete File" + file.path + " cleaned size: " + fileSize)
                return fileSize
            } else {
                //Log.d("DeleteRecursive", "CANT delete File" + file.getPath());
            }
        } catch (e: Exception) {
            //Log.wtf(TAG, "Exception while deleting file " + e.getMessage());
        }
        return 0
    }

    val randomCleaned: Float
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val timeNow = System.currentTimeMillis()
            val r = Random()
            val cleanNowMax = 150f + r.nextFloat() * 100f
            val lastTimeClean = prefs.getLong("lastTimeClean", timeNow - 12 * 60 * 60 * 1000)
            val editor = prefs.edit()
            editor.putLong("lastTimeClean", timeNow)
            editor.apply()
            var diff = timeNow - lastTimeClean
            if (diff > 60 * 60 * 1000) diff = (60 * 60 * 1000).toLong()
            return cleanNowMax * (diff.toFloat() / (60 * 60 * 1000))
        }

    private fun lastTimeUsingBoosting(): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val timeNow = System.currentTimeMillis()
        return prefs.getLong("lastTimeBoost", timeNow - 12 * 60 * 60 * 1000)
    }

    fun saveLastTimeBoosting() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val timeNow = System.currentTimeMillis()
        val editor = prefs.edit()
        editor.putLong("lastTimeBoost", timeNow)
        editor.apply()
    }

    private fun getAppNameFromPkgName(packageName: String): String {
        return try {
            val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationLabel(info) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    private fun getAppIconFromPkgName(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun killBackgroundApps(){
        val packages: List<ApplicationInfo>
        val pm: PackageManager = context.packageManager
        packages = pm.getInstalledApplications(0)
        val mActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val myPackage: String =
            context.applicationContext.packageName
        for (packageInfo in packages) {
            if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
            if (packageInfo.packageName == myPackage) continue
            mActivityManager.killBackgroundProcesses(packageInfo.packageName)
        }
    }

    fun boostPhone() {
        if (packageList.size > 0) {
            for (processInfo in packageList) {
                Log.wtf("Kill", "Try to kill " + processInfo.packageName)
                activityManager.killBackgroundProcesses(processInfo.packageName)
            }
        }
    }

    private fun sortPackageList() {
        Collections.sort(packageList, ProcessInfoComparator())
    }

    private fun addPackageToList(
        packageName: String,
        appName: String,
        timeUsed: Long,
        appIcon: Drawable?
    ) {
        var found = false
        for (i in packageList.indices) {
            if (packageList[i].packageName == packageName) {
                found = true
                packageList[i].setAppTotalUsageTime(packageList[i].appTotalUsageTime + timeUsed)
            }
        }
        if (!found) packageList.add(
            ProcessInfo(
                packageName, appName, timeUsed,
                appIcon!!
            )
        )
    }

    private fun createListOfRunningApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val lastBoostTime = lastTimeUsingBoosting()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, lastBoostTime, time)
            if (appList != null && appList.size > 0) {
                for (usageStats in appList) {
                    if (context.packageName == usageStats.packageName) continue
                    if (usageStats.lastTimeUsed > lastBoostTime) {
                        val packageName = usageStats.packageName
                        val appName = getAppNameFromPkgName(packageName)
                        if (appName.isEmpty()) continue
                        val timeUsed = usageStats.totalTimeInForeground
                        val appIcon = getAppIconFromPkgName(packageName)
                        if (timeUsed > 0) {
                            Log.wtf(
                                "Used",
                                "$appName($packageName) used time = $timeUsed"
                            )
                            addPackageToList(packageName, appName, timeUsed, appIcon)
                        }
                    }
                }
                calculatePercUsage()
                sortPackageList()
            }
        }
    }

    inner class ProcessInfoComparator : Comparator<ProcessInfo?> {
        override fun compare(o1: ProcessInfo?, o2: ProcessInfo?): Int {
            return (o2?.appTotalUsageTime!!).compareTo(o1?.appTotalUsageTime!!)
        }
    }

}

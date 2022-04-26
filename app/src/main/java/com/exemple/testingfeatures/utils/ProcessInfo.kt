package com.exemple.testingfeatures.utils

import android.graphics.drawable.Drawable


class ProcessInfo {
    var packageName: String
    var appName: String
    var appTotalUsageTime: Long = 0
    var lastTimeUsed: Long = 0
    var appIcon: Drawable
    var percUsage = 0
    var usageTime: String? = null
    var checked = false

    constructor(packageName: String, appName: String, appTotalUsageTime: Long, appIcon: Drawable) {
        this.packageName = packageName
        this.appName = appName
        this.appTotalUsageTime = appTotalUsageTime
        this.appIcon = appIcon
        usageTime = convertTimeToString(appTotalUsageTime)
    }

    constructor(
        packageName: String,
        appName: String,
        lastTimeUsed: Long,
        appIcon: Drawable,
        checked: Boolean
    ) {
        this.packageName = packageName
        this.appName = appName
        this.lastTimeUsed = lastTimeUsed
        this.appIcon = appIcon
        this.checked = checked
    }

    @JvmName("setAppTotalUsageTime1")
    fun setAppTotalUsageTime(appTotalUsageTime: Long) {
        this.appTotalUsageTime = appTotalUsageTime
        usageTime = convertTimeToString(appTotalUsageTime)
    }

    private fun convertTimeToString(time: Long): String {
        var time = time
        time /= 1000
        if (time < 60) {
            return "<1 minute"
        }
        if (time / 60 < 60) {
            return if (time / 60 == 1L)
                "1 minute"
            else
                    (time / 60).toString() + " minutes"
        }
        return if (time / 60 / 60 == 1L)
            "1 hour"
        else
                    (time / 60 / 60).toString() + " hours"
    }
}

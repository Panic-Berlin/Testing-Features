package com.exemple.testingfeatures.features.bigfiles

import android.graphics.drawable.Drawable
import java.io.File
import java.util.*


class RecyclerViewData {
    var imageId = 0
    var imageDrawable: Drawable? = null
    var stringName: String
    var stringSize: String
    var isSelect: Boolean
    var isDrawable: Boolean
    var fileSize: Long
    var thisFile: File?
    var parentDir: File? = null

    internal constructor(imageId: Int, stringName: String, thisFile: File?, isSelect: Boolean) {
        this.imageId = imageId
        this.isSelect = isSelect
        this.stringName = stringName
        fileSize = getFileSize(thisFile)
        isDrawable = false
        stringSize = convertSizeToString(fileSize)
        this.thisFile = thisFile
    }

    internal constructor(
        imageDrawable: Drawable?,
        stringName: String,
        thisFile: File?,
        isSelect: Boolean
    ) {
        this.imageDrawable = imageDrawable
        this.isSelect = isSelect
        this.stringName = stringName
        fileSize = getFileSize(thisFile)
        isDrawable = true
        stringSize = convertSizeToString(fileSize)
        this.thisFile = thisFile
    }

    private fun convertSizeToString(size: Long): String {
        val spc = arrayOf("B", "KB", "MB", "GB", "TB")
        var c = 0
        var prev = size.toFloat()
        while (prev / 1024 > 1 && c < 5) {
            prev /= 1024
            c++
        }
        return String.format("%.2f", prev) + " " + spc[c]
    }

    private fun getFileSize(file: File?): Long {
        if (file == null || !file.exists()) return 0
        if (!file.isDirectory()) return file.length()
        val dirs: MutableList<File> = LinkedList()
        dirs.add(file)
        var result: Long = 0
        while (!dirs.isEmpty()) {
            val dir: File = dirs.removeAt(0)
            if (!dir.exists()) continue
            val listFiles: Array<File> = dir.listFiles()
            if (listFiles == null || listFiles.size == 0) continue
            for (child in listFiles) {
                result += child.length()
                if (child.isDirectory()) dirs.add(child)
            }
        }
        return result
    }
}

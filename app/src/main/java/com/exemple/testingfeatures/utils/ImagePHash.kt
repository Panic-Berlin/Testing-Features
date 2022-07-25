package com.exemple.testingfeatures.utils

import android.graphics.*
import android.util.Log
import kotlin.math.sqrt


class ImagePHash {
    private var size = 32
    private var smallerSize = 8

    constructor() {
        initCoefficients()
    }

    constructor(size: Int, smallerSize: Int) {
        this.size = size
        this.smallerSize = smallerSize
        initCoefficients()
    }

    fun distance(s1: String?, s2: String?): Int {
        return if (s1 != null && s2 != null) {
            if (s1.length == s2.length && s1.length != 0 && s2.length != 0) {
                var counter = 0
                for (k in s1.indices) {
                    if (s1[k] != s2[k]) {
                        counter++
                    }
                }
                Log.d(TAG, "Distance: " + counter + " from " + s1.length)
                counter
            } else {
                Log.d(
                    TAG,
                    "Length of strings not equal: s1 = " + s1.length + " and s2 = " + s2.length + " or smaller then 0"
                )
                -1
            }
        } else -1
    }

    // Returns a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
    fun culcPHash(img: Bitmap?): String? {

        /* 1. Reduce size.
         * Like Average Hash, pHash starts with a small image.
         * However, the image is larger than 8x8; 32x32 is a good size.
         * This is really done to simplify the DCT computation and not
         * because it is needed to reduce the high frequencies.
         */
        var img = img
        img = resize(img, size, size)

        /* 2. Reduce color.
         * The image is reduced to a grayscale just to further simplify
         * the number of computations.
         */
        var hash = ""
        if (img != null) {
            img = grayscale(img)
            val vals = Array(size) {
                DoubleArray(
                    size
                )
            }
            for (x in 0 until img.width) {
                for (y in 0 until img.height) {
                    vals[x][y] = getBlue(img, x, y).toDouble()
                }
            }

            /* 3. Compute the DCT.
         * The DCT separates the image into a collection of frequencies
         * and scalars. While JPEG uses an 8x8 DCT, this algorithm uses
         * a 32x32 DCT.
         */
            val start = System.currentTimeMillis()
            val dctVals = applyDCT(vals)
            Log.d(TAG, (System.currentTimeMillis() - start).toString())

            /* 4. Reduce the DCT.
         * This is the magic step. While the DCT is 32x32, just keep the
         * top-left 8x8. Those represent the lowest frequencies in the
         * picture.
         */
            /* 5. Compute the average value.
         * Like the Average Hash, compute the mean DCT value (using only
         * the 8x8 DCT low-frequency values and excluding the first term
         * since the DC coefficient can be significantly different from
         * the other values and will throw off the average).
         */
            var total = 0.0
            for (x in 0 until smallerSize) {
                for (y in 0 until smallerSize) {
                    total += dctVals[x][y]
                }
            }
            total -= dctVals[0][0]
            val avg = total / (smallerSize * smallerSize - 1).toDouble()

            /* 6. Further reduce the DCT.
         * This is the magic step. Set the 64 hash bits to 0 or 1
         * depending on whether each of the 64 DCT values is above or
         * below the average value. The result doesn't tell us the
         * actual low frequencies; it just tells us the very-rough
         * relative scale of the frequencies to the mean. The result
         * will not vary as long as the overall structure of the image
         * remains the same; this can survive gamma and color histogram
         * adjustments without a problem.
         */for (x in 0 until smallerSize) {
                for (y in 0 until smallerSize) {
                    if (x != 0 && y != 0) {
                        hash += if (dctVals[x][y] > avg) "1" else "0"
                    }
                }
            }
            Log.d(TAG, "HASH result: $hash")
        } else {
            return null
        }
        return hash
    }

    fun resize(bm: Bitmap?, newHeight: Int, newWidth: Int): Bitmap? {
        // "RECREATE" THE NEW BITMAP
        var resizedBitmap: Bitmap? = null
        try {
            resizedBitmap = Bitmap.createScaledBitmap(bm!!, newWidth, newHeight, false)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return resizedBitmap
    }

    private fun grayscale(orginalBitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        val blackAndWhiteBitmap = orginalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val paint = Paint()
        paint.colorFilter = colorMatrixFilter
        val canvas = Canvas(blackAndWhiteBitmap)
        canvas.drawBitmap(blackAndWhiteBitmap, 0F,0F, paint)
        return blackAndWhiteBitmap
    }

    // DCT function stolen from http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
    private var c: DoubleArray? = null
    private fun initCoefficients() {
        c = DoubleArray(size)
        for (i in 1 until size) {
            c!![i] = 1.0
        }
        c!![0] = 1 / sqrt(2.0)
    }

    private fun applyDCT(f: Array<DoubleArray>): Array<DoubleArray> {
        val N = size
        val F = Array(N) {
            DoubleArray(
                N
            )
        }
        for (u in 0 until N) {
            for (v in 0 until N) {
                var sum = 0.0
                for (i in 0 until N) {
                    for (j in 0 until N) {
                        sum += Math.cos((2 * i + 1) / (2.0 * N) * u * Math.PI) * Math.cos(
                            (2 * j + 1) / (2.0 * N) * v * Math.PI
                        ) * f[i][j]
                    }
                }
                sum *= c!![u] * c!![v] / 4.0
                F[u][v] = sum
            }
        }
        return F
    }

    companion object {
        private const val TAG = "ImagePHASH"
        private fun getBlue(img: Bitmap?, x: Int, y: Int): Int {
            return img!!.getPixel(x, y) and 0xff
        }
    }
}

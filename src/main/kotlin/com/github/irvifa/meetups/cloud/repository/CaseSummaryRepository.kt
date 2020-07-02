package com.github.irvifa.meetups.cloud.repository

import com.github.irvifa.meetups.cloud.api.CaseSummary
import java.io.IOException
import java.math.BigInteger

interface CaseSummaryRepository {
    @Throws(IOException::class)
    fun updateCaseSummary(key: String, caseSummary: CaseSummary)

    @Throws(IOException::class)
    fun getCaseSummary(key: String): CaseSummary?
    fun generateKeyBasedOnLongitudeAndLatitude(latitude: Double, longitude: Double): String {
        val scaledX = (latitude + 180) / 360 * Math.pow(2.0, 16.0)
        val scaledY = (longitude + 90) / 180 * Math.pow(2.0, 16.0)
        val binX = Integer.toBinaryString(scaledX.toInt())
        val binY = Integer.toBinaryString(scaledY.toInt())
        val array = CharArray(binX.length + binY.length)
        var i = 0
        for (currentIndex in binX.indices) {
            array[i] = binX.get(currentIndex)
            i += 2
        }
        i = 1
        for (currentIndex in binY.indices) {
            array[i] = binY.get(currentIndex)
            i += 2
        }
        val sb = StringBuffer()
        for (currentIndex in array.indices) {
            sb.append(array[currentIndex])
        }
        return BigInteger(sb.toString(), 2).toString(10)
    }
}

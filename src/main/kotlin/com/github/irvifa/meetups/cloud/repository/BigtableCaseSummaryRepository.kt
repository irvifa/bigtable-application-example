package com.github.irvifa.meetups.cloud.repository

import com.github.irvifa.meetups.cloud.api.CaseReportPerRegion
import com.github.irvifa.meetups.cloud.api.CaseSummary
import com.google.common.io.Resources
import com.google.gson.Gson
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Table
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

class BigtableCaseSummaryRepository(
    private val table: Table,
    private val doesInitialize: Boolean
) : CaseSummaryRepository {

    override fun updateCaseSummary(key: String, caseSummary: CaseSummary) {
        val put =
            Put(Bytes.toBytes(key))
        val caseSummaryJson: String = gson.toJson(caseSummary)
        put.addColumn(
            Bytes.toBytes(COLUMN_FAMILY),
            Bytes.toBytes(COLUMN_NAME),
            Bytes.toBytes(caseSummaryJson)
        )

        try {
            table.put(put)
        } catch (e: IOException) {
            logger.error(
                "Can't update case for key: $key",
                e
            )
            throw e
        }
    }

    override fun getCaseSummary(key: String): CaseSummary? {
        try {
            val get = Get(Bytes.toBytes(key))
            val result = table.get(get)
            val isResultPresent = !result.isEmpty

            if (!isResultPresent) {
                return null
            }

            val value = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN_NAME))
            val valueStr = String(value, StandardCharsets.UTF_8)
            val summary = gson.fromJson(valueStr, CaseSummary::class.java)

            return summary
        } catch (e: IOException) {
            logger.error("Cannot get on table.", e)
            return null
        }
    }

    @Throws(IOException::class)
    private fun smokeTest(table: Table) {
        val get =
            Get(Bytes.toBytes("Any Row Does Not Matter."))
        table[get]
    }

    private fun initializeBigtable() {
        val resource =
            Thread.currentThread()
                .contextClassLoader.getResource("datasets_545466_1290623_covid19_italy_region.json")
        val json = Resources.toString(resource, StandardCharsets.UTF_8)
        val data = gson.fromJson(json, CaseSummary::class.java)
        val formattedData = mutableMapOf<String, MutableList<CaseReportPerRegion>>()
        data.summary.forEach {
            val key = generateKeyBasedOnLongitudeAndLatitude(it.latitude, it.longitude)
            val value = formattedData.getOrDefault(key, mutableListOf())
            value.add(it)
            formattedData.put(key, value)
        }
        formattedData.forEach { t, u -> updateCaseSummary(t, CaseSummary(u)) }
    }

    init {
        smokeTest(table)
        if (doesInitialize) {
            initializeBigtable()
        }
    }

    companion object {
        private const val COLUMN_FAMILY = "case"
        private const val COLUMN_NAME = "summary"
        private val gson = Gson()
        private val logger = LoggerFactory.getLogger(BigtableCaseSummaryRepository::class.java)
    }
}

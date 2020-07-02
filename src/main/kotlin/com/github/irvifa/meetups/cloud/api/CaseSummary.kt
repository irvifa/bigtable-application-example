package com.github.irvifa.meetups.cloud.api

class CaseReportPerRegion(
    val dateStr: String,
    val latitude: Double,
    val longitude: Double,
    val regionName: String,
    val hospitalizedPatients: Int,
    val icuPatients: Int,
    val totalHospitalizadPatients: Int,
    val homeConfinement: Int,
    val currentPositiveCases: Int,
    val newPositiveCases: Int,
    val recovered: Int,
    val deaths: Int,
    val totalPositiveCases: Int
)

class CaseSummary(
    val summary: MutableList<CaseReportPerRegion>
)

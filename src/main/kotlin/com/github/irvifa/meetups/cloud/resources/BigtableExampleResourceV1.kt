package com.github.irvifa.meetups.cloud.resources

import com.github.irvifa.meetups.cloud.api.ApiResponseV1
import com.github.irvifa.meetups.cloud.api.CaseReportPerRegion
import com.github.irvifa.meetups.cloud.api.CaseSummary
import com.github.irvifa.meetups.cloud.api.EmptyResponse
import com.github.irvifa.meetups.cloud.repository.CaseSummaryRepository
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/v1/summarizer")
class BigtableExampleResourceV1(private val caseSummaryRepository: CaseSummaryRepository) {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCaseSummarizer(request: CaseReportPerRegion): ApiResponseV1<EmptyResponse> {
        val key = caseSummaryRepository.generateKeyBasedOnLongitudeAndLatitude(
            request.latitude,
            request.longitude
        )
        val result = caseSummaryRepository.getCaseSummary(key)
        if (result != null) {
            result.summary.add(request)
        } else {
            caseSummaryRepository.updateCaseSummary(key, CaseSummary(mutableListOf(request)))
        }
        return ApiResponseV1(EmptyResponse())
    }

    @GET
    @Path("/{hashedRegionId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getNamespace(
        @PathParam("hashedRegionId") hashedRegionId: String
    ): ApiResponseV1<CaseSummary?> {
        return ApiResponseV1(caseSummaryRepository.getCaseSummary(hashedRegionId))
    }
}

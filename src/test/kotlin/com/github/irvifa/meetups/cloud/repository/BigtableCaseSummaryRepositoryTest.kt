package com.github.irvifa.meetups.cloud.repository

import io.mockk.MockKAnnotations
import org.apache.hadoop.hbase.client.Table
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.spyk
import org.junit.Before
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BigtableCaseSummaryRepositoryTest {
    @RelaxedMockK
    private lateinit var tableMock: Table
    private lateinit var underTest: BigtableCaseSummaryRepository

    @Before
    fun before() {
        MockKAnnotations.init(this)
        this.underTest = spyk(BigtableCaseSummaryRepository(tableMock, false))
    }

    @Test
    fun `generate key based on latitude and longitude`() {
        assertThat(this.underTest.generateKeyBasedOnLongitudeAndLatitude(45.6494354, 13.76813649)).isEqualTo("3372575544")
    }
}

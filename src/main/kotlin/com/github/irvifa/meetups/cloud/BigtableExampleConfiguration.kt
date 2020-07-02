package com.github.irvifa.meetups.cloud

import io.dropwizard.Configuration

class BigtableExampleConfiguration : Configuration() {
    private var bigtableConfig: BigtableConfig? = null

    fun getBigtableConfig(): BigtableConfig? {
        return bigtableConfig
    }

    class BigtableConfig {
        val instanceId: String? = null
        val projectId: String? = null
        val tableName: String? = null
    }
}

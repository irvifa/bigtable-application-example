package com.github.irvifa.meetups.cloud

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.irvifa.meetups.cloud.repository.BigtableCaseSummaryRepository
import com.github.irvifa.meetups.cloud.resources.BigtableExampleResourceV1
import com.google.cloud.bigtable.hbase.BigtableConfiguration
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.EnumSet
import javax.servlet.DispatcherType

class BigtableExampleApplication : Application<BigtableExampleConfiguration>() {
    override fun initialize(bootstrap: Bootstrap<BigtableExampleConfiguration>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
            bootstrap.configurationSourceProvider, EnvironmentVariableSubstitutor(true)
        )
    }

    @Throws(Exception::class)
    override fun run(configuration: BigtableExampleConfiguration, environment: Environment) {
        setupApplication(
            configuration,
            environment
        )
        allowCors(environment)
    }

    private fun doesInitialize(): Boolean {
        val env = System.getenv("ENVIRONMENT")
        return env == DEVELOPMENT_ENVIRONMENT
    }

    private fun setupApplication(
        configuration: BigtableExampleConfiguration,
        environment: Environment
    ) {
        val jersey = environment.jersey()
        val objectMapper = environment.objectMapper
        val bigtableConfiguration = Configuration()
        bigtableConfiguration[BigtableOptionsFactory.PROJECT_ID_KEY] = configuration.getBigtableConfig()!!.projectId!!
        bigtableConfiguration[BigtableOptionsFactory.INSTANCE_ID_KEY] = configuration.getBigtableConfig()!!.instanceId!!

        val connection = BigtableConfiguration.connect(bigtableConfiguration)
        val table = connection.getTable(TableName.valueOf(configuration.getBigtableConfig()!!.tableName!!))
        val caseSummaryRepository =
            BigtableCaseSummaryRepository(
                table,
                doesInitialize()
            )
        val bigtableExampleResourceV1 = BigtableExampleResourceV1(caseSummaryRepository)
        jersey.register(bigtableExampleResourceV1)
        objectMapper.registerModule(KotlinModule())
    }

    private fun allowCors(environment: Environment) {
        val cors = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
        cors.setInitParameter("allowedOrigins", "*")
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
        cors.setInitParameter("allowedMethods", "GET,POST,HEAD")

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
    }

    companion object {
        private const val DEVELOPMENT_ENVIRONMENT = "dev"
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            BigtableExampleApplication().run(*args)
        }
    }
}

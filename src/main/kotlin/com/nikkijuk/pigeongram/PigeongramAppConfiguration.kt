package com.nikkijuk.pigeongram

import com.azure.cosmos.CosmosClientBuilder
import com.azure.cosmos.DirectConnectionConfig
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration
import com.azure.spring.data.cosmos.config.CosmosConfig
import com.azure.spring.data.cosmos.core.ResponseDiagnostics
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories
import com.azure.spring.data.cosmos.repository.config.EnableReactiveCosmosRepositories
import com.nikkijuk.pigeongram.persistence.CosmosProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.lang.Nullable

@Configuration
@EnableConfigurationProperties(CosmosProperties::class)
@EnableCosmosRepositories
@EnableReactiveCosmosRepositories
@PropertySource("classpath:application.properties")
class PigeongramAppConfiguration : AbstractCosmosConfiguration() {

    @Autowired
    lateinit var properties: CosmosProperties

    @Bean
    fun cosmosClientBuilder(): CosmosClientBuilder {
        val directConnectionConfig: DirectConnectionConfig = DirectConnectionConfig.getDefaultConfig()
        return CosmosClientBuilder()
                .endpoint(properties.uri)
                .key(properties.key)
                .directMode(directConnectionConfig)
    }

    // TODO: why hardcoding here?
    override fun getDatabaseName(): String? = "pigeongramdb"

    @Bean
    override fun cosmosConfig(): CosmosConfig = CosmosConfig.builder()
                .responseDiagnosticsProcessor(ResponseDiagnosticsProcessorImplementation())
                .enableQueryMetrics(properties.isQueryMetricsEnabled)
                .build()


    private class ResponseDiagnosticsProcessorImplementation : ResponseDiagnosticsProcessor {
        @Override
        override fun processResponseDiagnostics(@Nullable responseDiagnostics: ResponseDiagnostics?) {
            logger.info("Response Diagnostics {}", responseDiagnostics)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PigeongramAppConfiguration::class.java)
    }
}
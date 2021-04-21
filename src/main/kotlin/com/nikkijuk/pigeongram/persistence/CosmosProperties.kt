package com.nikkijuk.pigeongram.persistence

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * All these settings are to be filled and thus lateinit is used here
 */
@ConfigurationProperties(prefix = "cosmos")
class CosmosProperties {
    lateinit var uri: String
    lateinit var key: String
    lateinit var secondaryKey: String
    var isQueryMetricsEnabled = false
}
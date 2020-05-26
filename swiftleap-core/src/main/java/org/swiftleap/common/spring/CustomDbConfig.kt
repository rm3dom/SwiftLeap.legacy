package org.swiftleap.common.spring

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties

open class CustomDataSourceProperties : DataSourceProperties() {
    open var jpaProperties : MutableMap<String, String> = mutableMapOf()
    open var flywayEnabled : Boolean = false
}
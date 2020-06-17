package org.swiftleap.common.service

import java.security.Principal

interface ExecutionContext {
    fun getCurrentTenantId() : Int

    fun getCurrentUser() : Principal
}
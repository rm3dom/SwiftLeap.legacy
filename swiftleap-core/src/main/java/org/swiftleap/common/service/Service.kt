package org.swiftleap.common.service

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.swiftleap.common.security.SecurityContext
import java.security.Principal

@Primary
@Service
open class ExecutionContextService : ExecutionContext {
    override fun getCurrentTenantId(): Int =
        SecurityContext.getTenantId()

    override fun getCurrentUser(): Principal =
        SecurityContext.getPrincipal()
}
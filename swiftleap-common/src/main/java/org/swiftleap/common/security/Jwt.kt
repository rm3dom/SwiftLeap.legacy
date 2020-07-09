package org.swiftleap.common.security

import com.fasterxml.jackson.annotation.JsonIgnore
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

object ClaimTypes {
    const val securityLevel = "SecurityLevel";
    const val uniqueName = "unique_name";
    const val authenticationInstant =  "authenticationinstant";
    const val authenticationMethod =  "authenticationmethod";
    const val cookiePath =  "cookiepath";
    const val denyOnlyPrimarySid =  "denyonlyprimarysid";
    const val denyOnlyPrimaryGroupSid =  "denyonlyprimarygroupsid";
    const val denyOnlyWindowsDeviceGroup =  "denyonlywindowsdevicegroup";
    const val dsa =  "dsa";
    const val expiration =  "expiration";
    const val expired =  "expired";
    const val groupSid =  "groupsid";
    const val isPersistent =  "ispersistent";
    const val primaryGroupSid =  "primarygroupsid";
    const val primarySid =  "primarysid";
    const val role =  "role";
    const val serialNumber =  "serialnumber";
    const val userData =  "userdata";
    const val surname = "surname";
    const val givenName = "givenname";
    const val version =  "version";
    const val tenantId = "tenantId";
}


fun Claims.getFirst(key: String) : String? =
    when(val any = this[key]) {
        is String -> any
        is Iterable<*> -> any.firstOrNull()?.toString()
        else -> null
    }

fun Claims.getAll(key: String) : Iterable<String> =
        when(val any = this[key]) {
            is String -> listOf(any)
            is Iterable<*> -> any.toList().map { it.toString() }
            else -> emptyList()
        }

private class SecRoleCodeImpl(val role: String) : SecRoleCode {
    override fun getCode(): String {
        return role
    }
}

class ClaimsPrincipal(@JsonIgnore val claims : Claims) : UserPrincipal {
    private val _tenantId : Int = claims.getFirst(ClaimTypes.tenantId)?.toIntOrNull() ?: 0
    private val _uniqueName : String = claims.getFirst(ClaimTypes.uniqueName) ?: ""
    private val _name : String = claims.getFirst(ClaimTypes.givenName) ?: ""
    private val _surname : String = claims.getFirst(ClaimTypes.surname) ?: ""
    private val _roles = claims.getAll(ClaimTypes.role).map { SecRoleCodeImpl(it) }

    override fun getName(): String  = _uniqueName

    override fun getUserName(): String = _uniqueName

    override fun setTenantId(tenantId: Int?) = Unit

    override fun getPartyId(): Long = 0

    override fun getDescription(): String = "$_name $_surname"

    override fun getTenantId(): Int = _tenantId

    override fun getPrincipalRoles(): Collection<SecRoleCode> = _roles
}

object Jwt {
    fun decode(token: String, signingKey: ByteArray) : ClaimsPrincipal {
        val parser = Jwts.parser()
        parser.setSigningKey(signingKey)
        parser.setAllowedClockSkewSeconds(360)
        return ClaimsPrincipal(parser.parseClaimsJws(token).body)
    }

    fun encode(user: UserPrincipal,
               signingKey: ByteArray,
               audience: String,
               issuer: String) =
        Jwts.builder()
                .claim(Claims.ID, UUID.randomUUID().toString())
                .claim(ClaimTypes.uniqueName, user.name)
                .claim(ClaimTypes.givenName, user.description)
                .claim(ClaimTypes.tenantId, user.tenantId)
                .claim(ClaimTypes.role, user.principalRoles.map { r -> r.code })
                .setAudience(audience)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact()
}
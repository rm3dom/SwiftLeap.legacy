/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.common.security.impl;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swiftleap.common.config.ConfigService;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.persistance.SequenceGenerator;
import org.swiftleap.common.security.*;
import org.swiftleap.common.security.impl.model.*;
import org.swiftleap.common.service.BadRequestException;
import org.swiftleap.common.service.ServiceException;
import org.swiftleap.common.service.SystemErrorException;
import org.swiftleap.common.types.Range;
import org.swiftleap.common.util.StringUtil;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/04.
 */
@Primary
@Service
public class SecurityServiceImpl implements SecurityService {
    final static HashMap<String, Session> sessions = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);
    @Autowired
    EncryptionController encryptionController;
    @Autowired
    UserDao userDao;
    @Autowired
    SecRoleDefineDao secRoleDefineDao;
    @Autowired
    UserSecRoleDao userSecRoleDao;
    @Autowired
    SecRoleDelegateDao secRoleDelegateDao;
    @Autowired
    ConfigService configService;
    @Autowired
    TenantDao tenantDao;
    @Autowired
    SequenceGenerator sequenceGenerator;

    @Value(value = PropKeys._SECURITY_LDAP_ENABLED)
    private boolean ldapEnabled = false;
    @Value(value = PropKeys._SECURITY_LDAP_DNBASE)
    private String ldapDnBase = "DC=PHD,DC=com,DC=au";
    @Value(value = PropKeys._SECURITY_LDAP_HOST)
    private String ldapHost = "10.20.60.10";
    @Value(value = PropKeys._SECURITY_LDAP_PORT)
    private String ldapPort = "389";
    @Value(value = PropKeys._SECURITY_LDAP_USERDN)
    private String ldapUserDn = "CN=PHD System,CN=Users,DC=PHD,DC=com,DC=au";
    @Value(value = PropKeys._SECURITY_LDAP_PASSWORD)
    private String ldapUserPassword = "Coffee350";
    @Value(value = PropKeys._SECURITY_LDAP_FILTER)
    private String ldapSearchFilter = "(&(objectClass=user)(objectCategory=person)(sAMAccountName={user}))";
    @Value(value = PropKeys._SECURITY_ADMIN_USERNAME)
    private String adminUserName = "sysadm";
    @Value(value = PropKeys._SECURITY_JWT_SIGNING_KEY)
    private String jwtSigningKey = "";
    @Value(value = PropKeys._SECURITY_JWT_AUDIENCE)
    private String jwtAudience = "";
    @Value(value = PropKeys._SECURITY_JWT_ISSUER)
    private String jwtIssuer = "";

    public static boolean contains(String haystack, String needel) {
        if (StringUtil.isNullOrWhites(needel))
            return true;
        return haystack != null && haystack.toLowerCase().contains(needel);
    }

    private void cleanRequest(TenantRequest request) {
        String countryCode = request.getCountryCode();
        if (StringUtil.isNullOrWhites(countryCode))
            request.setCountryCode(SecurityContext.getCountryCode());

        if (StringUtil.isNullOrWhites(request.getFqdn())) {
            request.setFqdn(null);
        }
    }

    @Override
    public Tenant createTenant(TenantRequest request) {
        if (StringUtil.isNullOrWhites(request.getName()))
            throw new BadRequestException("Tenant Name is required");

        Integer id = request.getId();
        if (id == null)
            id = sequenceGenerator.getSequence("TenantId", 1).getStart();

        cleanRequest(request);

        TenantDbo tenant = new TenantDbo();
        tenant.setId(id);
        tenant.setPartyId(request.getPartyId());
        tenant.setName(request.getName());
        tenant.setCountryCode(request.getCountryCode());
        tenant.setFqdn(request.getFqdn());
        tenant = tenantDao.persist(tenant);

        if (request.isActivated())
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        else
            tenant.setStatus(Tenant.TenantStatus.INACTIVE);

        val t = tenant;
        if (request.getConfig() != null)
            request.getConfig().forEach((k, v) -> t.getConfig().set(k, v));

        tenantDao.flush();

        //Impersonate as the new tenant and create the admin user
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            SecurityContext.doImpersonation(tenant, () -> createUser(
                    UserRequest.builder()
                            .userName(request.getUserName())
                            .firstName(request.getFirstName())
                            .surname(request.getSurname())
                            .activated(true)
                            .description("Super User")
                            .email(request.getEmail())
                            .password(request.getPassword())
                            .roles(new String[]{"sysadm"})
                            .build()));
        }

        return tenant;
    }

    @Override
    public Tenant updateTenant(TenantRequest request) {
        if (request.getName() == null
                || request.getId() == null)
            throw new BadRequestException("Invalid tenant info");

        cleanRequest(request);

        TenantDbo tenant = tenantDao.findById(request.getId());
        tenant.setFqdn(request.getFqdn());
        tenant.setCountryCode(request.getCountryCode());
        tenant.setName(request.getName());
        tenant.setPartyId(request.getPartyId());
        if (request.isActivated())
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        else
            tenant.setStatus(Tenant.TenantStatus.INACTIVE);

        return tenant;
    }

    @Override
    public Tenant getTenantByFqdn(String fqdn) {
        //cleanup
        fqdn = fqdn.replaceAll("http[s]?://", "").trim();
        int colon = fqdn.indexOf(':');
        if (colon > 0)
            fqdn = fqdn.substring(0, colon);
        return tenantDao.findByFqdn(fqdn);
    }

    //@Transactional(readOnly = true)
    @Override
    public Tenant getTenant(Integer tenantId) {
        return tenantDao.findById(tenantId);
    }

    @Override
    public Stream<? extends Tenant> findTenants() {
        return tenantDao.findAll();
    }

    @Override
    public Stream<SecRoleDefineDbo> getSecurityRoles() {
        return secRoleDefineDao.findAll();
    }

    @Override
    public SecRole getSecurityRole(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return secRoleDefineDao.findByCode(code);
    }

    @Override
    public SecRole getSecurityRole(Long roleId) {
        if (roleId == null || roleId < 1) {
            return null;
        }
        return secRoleDefineDao.findById(roleId);
    }

    @Override
    public void deleteSecurityRole(SecRole role) {
        if (role == null) {
            return;
        }
        secRoleDefineDao.delete(role);
    }

    @Override
    public SecRole createSecurityRole(String code, String name, String description, SecRole.Status status) {

        if (code == null
                || name == null
                || description == null
                || status == null) {
            throw new NullPointerException("Null");
        }

        code = code.toUpperCase();

        SecRoleDefineDbo roleDef = new SecRoleDefineDbo();
        roleDef.setCode(code);
        roleDef.setName(name);
        roleDef.setDescription(description);
        roleDef.setStatus(status);

        roleDef = secRoleDefineDao.persist(roleDef);
        secRoleDefineDao.flush();
        secRoleDefineDao.refresh(roleDef);
        return roleDef;
    }

    @Override
    public SecRole createSecurityDelegate(SecRole delegatingRole, SecRole delegateTo) {
        if (delegatingRole == null
                || delegateTo == null
                || delegatingRole.getCode() == null
                || delegateTo.getCode() == null) {
            return null;
        }

        if (!(delegatingRole instanceof SecRoleDefineDbo)) {
            delegatingRole = getSecurityRole(delegatingRole.getCode());
        }

        if (!(delegateTo instanceof SecRoleDefineDbo)) {
            delegateTo = getSecurityRole(delegateTo.getCode());
        }

        SecRoleDelegateDbo del = new SecRoleDelegateDbo(delegatingRole, delegateTo);

        secRoleDelegateDao.persist(del);

        delegatingRole.getDelegates().add(del);

        return del;
    }

    @Override
    public SecRole createSecurityRole(User user, SecRole secRole) {
        if (user == null
                || secRole == null
                || secRole.getCode() == null) {
            throw new NullPointerException("Null");
        }

        SecurityRolesCollection<SecRole> userRoles = user.getSecurityRoles();

        SecRole hasRole = userRoles.findByCode(secRole.getCode());
        if (hasRole != null) {
            return hasRole;
        }

        secRole = getSecurityRole(secRole.getCode());

        UserSecRoleDbo userSecRole = new UserSecRoleDbo();
        userSecRole.setUser(user);
        userSecRole.setRole(secRole);

        userRoles.add(userSecRole);

        userSecRoleDao.persist(userSecRole);

        return userSecRole;
    }

    @Override
    public Collection<SecRole> createSecurityRole(User user, SecRole... secRoles) {
        List<SecRole> ret = new ArrayList<>();
        for (SecRole r : secRoles) {
            ret.add(createSecurityRole(user, r));
        }
        return ret;
    }

    private void validate(User user, UserRequest request) {
        if (StringUtil.isNullOrWhites(request.getUserName()))
            throw new BadRequestException("UserName is required");

        if (user == null && StringUtil.isNullOrWhites(request.getPassword()))
            throw new BadRequestException("Password is required");

        if (!StringUtil.isNullOrWhites(request.getEmail())) {
            User other = findUserByCred(null, request.getEmail(), null);
            if (user != null && other != null && !other.getId().equals(user.getId()))
                throw new BadRequestException("User with Email already exists: " + request.getEmail());
            else if (other != null && user == null)
                throw new BadRequestException("User with Email already exists: " + request.getEmail());
        } else
            request.setEmail(null);

        User other = findUserByCred(request.getUserName(), null, null);
        if (user != null && other != null && !other.getId().equals(user.getId()))
            throw new BadRequestException("User with user name already exists: " + request.getUserName());
        else if (other != null && user == null)
            throw new BadRequestException("User with user name already exists: " + request.getUserName());
    }

    @Override
    public User updateUser(UserRequest request) {
        User user = getUser(request.getId());
        if (user == null)
            throw new BadRequestException("Invalid user id");

        validate(user, request);

        if (!StringUtil.isNullOrWhites(request.getPassword()) && !request.getPassword().equals("************"))
            user.setPassword(request.getPassword());

        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setSurname(request.getSurname());

        if (request.isActivated())
            user.setStatus(User.UserStatus.ACTIVE);
        else
            user.setStatus(User.UserStatus.INACTIVE);

        if (request.getRoles() != null && request.getRoles().length > 0)
            user.getSecurityRoles().setRoles(request.getRoles());

        return user;
    }

    @Override
    public User createUser(UserRequest request) {
        validate(null, request);

        UserDbo userDbo = new UserDbo();

        userDbo.setPartyId(request.getPartyId());
        userDbo.setDescription(request.getDescription());
        userDbo.setCredentials(request.getUserName(), request.getEmail(), request.getPassword(), false);
        userDbo.setFirstName(request.getFirstName());
        userDbo.setSurname(request.getSurname());
        if (request.isActivated())
            userDbo.setStatus(User.UserStatus.ACTIVE);
        else
            userDbo.setStatus(User.UserStatus.INACTIVE);

        if (userDbo.getTenantId() == null || userDbo.getTenantId() < 1)
            userDbo.setTenantId(SecurityContext.getTenantId());

        userDbo = userDao.persist(userDbo);
        userDbo = userDao.refresh(userDbo);

        userDbo.getSecurityRoles().addAll(request.getRoles());

        return userDbo;
    }

    @Override
    public boolean deleteUser(User user) {
        userDao.delete(user);
        return true;
    }

    @Override
    public Stream<UserDbo> findUsersByParty(Long partyId) {
        return userDao.findUsersByParty(partyId);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(Long userId) {
        return userDao.findById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserByCred(String userName, String email, String password) {
        String encryptedPassword = null;
        if (password != null) {
            try {
                //TODO Older systems do indicate if the password is obfuscated.
                //String clearPassword = encc.unObfuscatePassword(password);
                encryptedPassword = encryptionController.encryptPassword(password);
            } catch (Exception ex) {
                throw new SystemErrorException(ex);
            }
        }

        return userDao.findUserByCred(userName, email, encryptedPassword);
    }

    @Override
    public Session login(String userName, String password, Map<String, Object> opts) throws ManagedSecurityException {
        User user = null;
        if (ldapEnabled && !userName.equalsIgnoreCase(adminUserName))
            user = loginLdap(userName, password);
        else {
            user = findUserByCred(null, userName, password);
            if (user == null)
                user = findUserByCred(userName, null, password);
        }

        Session session = createSession(user);

        synchronized (sessions) {
            sessions.put(session.getSessionId(), session);
        }

        return session;
    }

    @Override
    public void deleteSession(String sessionId) {
        synchronized (sessions) {
            sessions.remove(sessionId);
        }
    }

    @Override
    public Session loginApi(String userName, String password, Map<String, Object> opts) throws ManagedSecurityException {
        User user = findUserByCred(userName, null, password);
        return createSession(user);
    }

    @Override
    public Session loginApi(String apiKey, Map<String, Object> opts) throws ManagedSecurityException {
        throw new ServiceException("Not supported");
    }

    private Session createSession(User user) throws ManagedSecurityException {
        if (user == null)
            throw new ManagedSecurityException("Invalid username or password");

        if (user.getStatus() != User.UserStatus.ACTIVE)
            throw new ManagedSecurityException("User disabled");

        String sessionId = Jwt.INSTANCE.encode(user, jwtSigningKey.getBytes(), jwtAudience, jwtIssuer);
        Session session = new Session();
        session.setScheme("Bearer");
        session.setSessionId(sessionId);
        session.setUser(user);
        session.setSessionCreated(new Date());
        return session;
    }

    private User loginLdap(String userName, String password) {
        Function<DirContext, Boolean> closeContext = (dir) -> {
            if (dir != null) {
                try {
                    dir.close();
                } catch (NamingException e) {
                }
            }
            return true;
        };

        DirContext ldapContext = null;
        try {
            Properties ldapEnv = new Properties();
            ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            ldapEnv.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
            ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
            ldapEnv.put(Context.SECURITY_PRINCIPAL, ldapUserDn);
            ldapEnv.put(Context.SECURITY_CREDENTIALS, Obs.decryptIfObs(ldapUserPassword));
            //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
            ldapContext = new InitialDirContext(ldapEnv);

            // Create the search controls
            SearchControls searchCtls = new SearchControls();

            //Specify the attributes to return
            String returnedAtts[] = {"sn", "givenName", "sAMAccountName"};
            searchCtls.setReturningAttributes(returnedAtts);

            //Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //specify the LDAP search filter
            String userFilter = ldapSearchFilter.replace("{user}", userName);

            //Specify the Base for the search
            String searchBase = ldapDnBase;
            //initialize counter to total the results
            int totalResults = 0;

            // Search for objects using the filter
            NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, userFilter, searchCtls);

            //Loop through the search results
            while (answer.hasMoreElements()) {
                SearchResult sr = answer.next();
                Attributes attrs = sr.getAttributes();

                ldapEnv.put(Context.SECURITY_PRINCIPAL, sr.getNameInNamespace());
                ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
                DirContext ldapLoginContext = null;
                try {
                    ldapLoginContext = new InitialDirContext(ldapEnv);

                    User user = findUserByCred(userName, null, null);

                    if (user == null)
                        user = createUser(UserRequest.builder()
                                .firstName(attrs.get("givenName") == null ? userName : Objects.toString(attrs.get("givenName").get()))
                                .password(password)
                                .activated(false)
                                .userName(userName)
                                .build());

                    return user;

                } catch (Exception ex) {
                    LOG.error("Failed to authenticate to Active Directory: " + userName, ex);
                } finally {
                    closeContext.apply(ldapLoginContext);
                }
            }

        } catch (NamingException ex) {
            throw new SecurityException("Unable to connect to Active Directory", ex);
        } finally {
            closeContext.apply(ldapContext);
        }
        return null;
    }

    @Override
    public Session getSession(String sessionId) {
        if(StringUtil.isNullOrWhites(sessionId))
            return null;
        Session session = null;
        synchronized (sessions) {
             session = sessions.get(sessionId);
             if(session != null)
                 return session;
        }

        try {
            ClaimsPrincipal user = Jwt.INSTANCE.decode(sessionId, jwtSigningKey.getBytes());
            if(user != null) {
                session = new Session();
                session.setScheme("Bearer");
                session.setUser(user);
                session.setSessionCreated(new Date());
                session.setSessionId(sessionId);
                return session;
            }
        } catch (Exception ex) {
            //Nothing, login failed.
        }
        return null;
    }

    @Override
    public Stream<UserDbo> findByRoles(String[] roles) {
        return userDao.findAll()
                .filter(u -> u.getSecurityRoles().hasAnyRole(roles));
    }

    @Override
    public Stream<? extends User> find(String any, User.UserStatus status, Range range) {
        String s = any.toLowerCase();
        return userDao.findAll()
                .filter(u -> (contains(u.getEmail(), s)
                        || contains(u.getUserName(), s)
                        || contains(u.getFirstName(), s)
                        || contains(u.getSurname(), s))
                        && (status == null || status == User.UserStatus.UNKNOWN || status == u.getStatus())
                )
                .skip(range.getStart())
                .limit(range.getCount());
    }
}

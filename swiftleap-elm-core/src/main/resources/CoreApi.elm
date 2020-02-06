module CoreApi exposing (apiLogin, apiRestart, apiRoles, apiSaveTenant, apiTenants, apiUpdate, apiUpdateInfo, apiUpdateTenant, apiUser, apiUserSession, apiUsers)

import HostApi exposing (HostApiConfig)
import Http
import Json.Decode as JD
import Task exposing (Task)
import Types.AuthRequest as AuthRequest exposing (AuthRequest)
import Types.NameSearchRequest as NameSearchRequest exposing (NameSearchRequest)
import Types.Pair as Pair exposing (Pair)
import Types.SearchUsersRequest as SearchUsersRequest exposing (SearchUsersRequest)
import Types.Tenant as Tenant exposing (Tenant)
import Types.TenantRequest as TenantRequest exposing (TenantRequest)
import Types.UpdateInfo as UpdateInfo exposing (UpdateInfo)
import Types.User as User exposing (User)
import Types.UserRequest as UserRequest exposing (UserRequest)


apiLogin : AuthRequest -> HostApiConfig -> Task Http.Error User
apiLogin request =
    HostApi.post
        "security/login"
        (AuthRequest.encode request)
        User.decode


apiTenants : NameSearchRequest -> HostApiConfig -> Task Http.Error (List Tenant)
apiTenants request =
    HostApi.post
        "system/tenants"
        (NameSearchRequest.encode request)
        (JD.list Tenant.decode)


apiUsers : SearchUsersRequest -> HostApiConfig -> Task Http.Error (List User)
apiUsers request =
    HostApi.post
        "security/users"
        (SearchUsersRequest.encode request)
        (JD.list User.decode)


apiRoles : HostApiConfig -> Task Http.Error (List Pair)
apiRoles =
    HostApi.get
        "security/roles"
        (JD.list Pair.decode)


apiUpdateInfo : HostApiConfig -> Task Http.Error UpdateInfo
apiUpdateInfo =
    HostApi.get
        "system/update/info"
        UpdateInfo.decode


apiUpdate : HostApiConfig -> Task Http.Error String
apiUpdate =
    HostApi.getExpectNothing
        "system/update"


apiRestart : HostApiConfig -> Task Http.Error String
apiRestart =
    HostApi.getExpectNothing
        "system/restart"


apiSaveTenant : TenantRequest -> HostApiConfig -> Task Http.Error Tenant
apiSaveTenant request =
    HostApi.post
        "system/savetenant"
        (TenantRequest.encode request)
        Tenant.decode


apiUpdateTenant : TenantRequest -> HostApiConfig -> Task Http.Error Tenant
apiUpdateTenant request =
    HostApi.post
        "system/createtenant"
        (TenantRequest.encode request)
        Tenant.decode


apiUserSession : HostApiConfig -> Task Http.Error (Maybe User)
apiUserSession =
    HostApi.get
        "security/sessions"
        (JD.maybe User.decode)


apiUser : UserRequest -> HostApiConfig -> Task Http.Error User
apiUser request =
    HostApi.post
        "security/user"
        (UserRequest.encode request)
        User.decode

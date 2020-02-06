module Core exposing
    ( Core
    , CoreJob
    , Msg(..)
    , actionsToJob
    , getHost
    , getSystem
    , init
    , jobToCmd
    , setSystem
    , setTime
    , toastOnError
    , update
    )

import Alfred.Logic
import CoreApi
import HostApi exposing (HostApiConfig)
import Http
import Job exposing (..)
import Model.System as System exposing (System)
import Navigation
import Security as Security exposing (Security)
import Time exposing (Time)
import Toast as Toast exposing (Toast)
import Types.Flags exposing (Flags)
import Types.NameSearchRequest
import Types.Pair exposing (Pair)
import Types.SearchUsersRequest
import Types.Tenant exposing (Tenant)
import Types.UpdateInfo exposing (UpdateInfo)
import Types.User exposing (User)


type alias CoreJob msg =
    Job msg NoRoute Action


type Msg
    = NoOp
    | NoOpString String
    | ToastMsg Toast.ToastMsg
    | SecurityMsg Security.Msg
    | ReceiveUserSession (Maybe User)
    | ReceiveUsers (List User)
    | ReceiveTenants (List Tenant)
    | ReceiveRoles (List Pair)
    | ReceiveUpdateInfo UpdateInfo
    | LoggedIn


type alias Core =
    { system : System
    , toast : Toast
    , security : Security
    }


init : Flags -> Navigation.Location -> ( Core, CoreJob Msg )
init flags location =
    let
        job =
            Job.init
                |> Job.addApi ReceiveUpdateInfo CoreApi.apiUpdateInfo
                |> Job.addApi ReceiveTenants (CoreApi.apiTenants Types.NameSearchRequest.init)
                |> Alfred.Logic.when (not (String.isEmpty flags.sessionId)) (Job.addApi ReceiveUserSession CoreApi.apiUserSession)
    in
    ( { system = System.init flags location
      , toast = Toast.init
      , security = Security.init
      }
    , job
    )


getHost : Core -> HostApiConfig
getHost core =
    core.system.host


getSystem : Core -> System
getSystem core =
    core.system


setSystem : System -> Core -> Core
setSystem system core =
    { core | system = system }


setTime : Time -> Core -> Core
setTime time core =
    { core | system = System.setTime time core.system }


update : Msg -> Core -> ( Core, CoreJob Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Job.init )

        NoOpString comp ->
            ( model, Job.init )

        ToastMsg msg ->
            ( { model | toast = Toast.update model.system.time msg model.toast }
            , Job.init
            )

        LoggedIn ->
            ( model
            , Job.init
            )

        SecurityMsg msg ->
            let
                ( system, security, job ) =
                    Security.update model.system msg model.security
            in
            ( { model | system = system, security = security }
            , Job.map SecurityMsg job
            )

        ReceiveUserSession user ->
            ( { model | system = System.setUser user model.system }
            , Job.addAction Job.LoggedIn Job.init
            )

        ReceiveUsers users ->
            ( { model | system = System.setUsers users model.system }, Job.init )

        ReceiveTenants tenants ->
            ( { model | system = System.setTenants tenants model.system }, Job.init )

        ReceiveRoles roles ->
            ( { model | system = System.setRoles roles model.system }, Job.init )

        ReceiveUpdateInfo updateInfo ->
            ( { model | system = System.setUpdateInfo updateInfo model.system }, Job.init )


actionsToJob : List Job.Action -> CoreJob Msg
actionsToJob actions =
    let
        addAction action job =
            case action of
                Job.ToastInfo msg ->
                    Job.addMsg (ToastMsg (Toast.ShowInfo msg)) job

                Job.LoaderShow msg ->
                    Job.addMsg (ToastMsg (Toast.ShowLoader msg)) job

                Job.LoaderHide ->
                    Job.addMsg (ToastMsg Toast.HideLoader) job

                Job.ToastWarning msg ->
                    Job.addMsg (ToastMsg (Toast.ShowWarning msg)) job

                Job.ToastError msg ->
                    Job.addMsg (ToastMsg (Toast.ShowError msg)) job

                Job.ToastHide ->
                    Job.addMsg (ToastMsg Toast.HideToast) job

                Job.LoggedIn ->
                    job
                        |> Job.addApi ReceiveUsers (CoreApi.apiUsers Types.SearchUsersRequest.init)
                        |> Job.addApi ReceiveRoles CoreApi.apiRoles
                        |> Job.addApi ReceiveUpdateInfo CoreApi.apiUpdateInfo
                        |> Job.addMsg LoggedIn

                Job.ReloadRepo ->
                    job
    in
    List.foldl addAction Job.init actions


toastOnError : Result Http.Error Msg -> Msg
toastOnError task =
    case task of
        Err err ->
            ToastMsg (Toast.ShowHttpError err)

        Ok msg ->
            msg


jobToCmd : HostApi.HostApiConfig -> CoreJob Msg -> Cmd Msg
jobToCmd host job =
    job
        |> Job.union (actionsToJob job.actions)
        |> Job.attempt toastOnError (\_ -> NoOp) host

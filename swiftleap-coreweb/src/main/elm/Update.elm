module Update exposing (coreJobToCmd, init, jobToCmd, loadJob, subscriptions, ticks, toastOnError, update)

import AppJob exposing (AppJob)
import Components.Me as Me
import Components.Tenants as Tenants
import Components.Updates as Updates
import Components.Users as Users
import Core as Core
import HostApi
import Http
import Job exposing (CoreJob)
import Model exposing (..)
import Msg exposing (..)
import Navigation
import Ports
import Routing
import Time exposing (second)
import Toast
import Types.Flags exposing (Flags)


ticks : number
ticks =
    3


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.batch
        [ Time.every (ticks * Time.second) Tick
        ]


init : Flags -> Navigation.Location -> ( Model, Cmd Msg )
init flags location =
    let
        ( model, job ) =
            Model.init flags location
    in
    ( model, jobToCmd (Core.getHost model.core) job )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    let
        --Log everything except ticks
        _ =
            case msg of
                Tick _ ->
                    msg

                _ ->
                    msg
                    --Debug.log "#msg:" msg

        host =
            Core.getHost model.core
    in
    case msg of
        NoOp ->
            ( model, Cmd.none )

        Tick time ->
            ( { model | core = Core.setTime time model.core }
            , Job.init |> jobToCmd host
            )

        UrlChange location ->
            let
                newState =
                    Routing.updateLocation location model.routing

                route =
                    Routing.currentRoute newState
            in
            ( { model | routing = newState }, Cmd.none )

        GotoRoute route ->
            let
                ( newState, cmd ) =
                    Routing.gotoRoute route model.routing
            in
            ( { model | routing = newState }, cmd )

        UsersMsg msg ->
            let
                ( system, users, job ) =
                    Users.update model.core.system msg model.users

                core =
                    Core.setSystem system model.core
            in
            ( { model | core = core, users = users }
            , job |> Job.map Msg.UsersMsg |> jobToCmd host
            )

        MeMsg msg ->
            let
                ( system, me, job ) =
                    Me.update model.core.system msg model.me

                core =
                    Core.setSystem system model.core
            in
            ( { model | core = core, me = me }
            , job |> Job.map Msg.MeMsg |> jobToCmd host
            )

        TenantsMsg msg ->
            let
                ( system, tenants, job ) =
                    Tenants.update model.core.system msg model.tenants

                core =
                    Core.setSystem system model.core
            in
            ( { model | core = core, tenants = tenants }
            , job |> Job.map Msg.TenantsMsg |> jobToCmd host
            )

        UpdatesMsg msg ->
            let
                ( system, updates, job ) =
                    Updates.update model.core.system msg model.updates

                core =
                    Core.setSystem system model.core
            in
            ( { model | core = core, updates = updates }
            , job |> Job.map Msg.UpdatesMsg |> jobToCmd host
            )

        CoreMsg msg ->
            let
                ( core, coreJob ) =
                    Core.update msg model.core

                newModel =
                    case msg of
                        Core.ReceiveUserSession _ ->
                            { model | core = core, me = Me.initLoggedIn core.system }

                        Core.SecurityMsg _ ->
                            { model | core = core, me = Me.initLoggedIn core.system }

                        Core.LoggedIn ->
                            { model | core = core, me = Me.initLoggedIn core.system }

                        _ ->
                            { model | core = core }
            in
            --We have to use the host returned in update above
            ( newModel, coreJobToCmd (Core.getHost core) coreJob )


toastOnError : Result Http.Error Msg -> Msg
toastOnError task =
    case task of
        Err err ->
            CoreMsg (Core.ToastMsg (Toast.ShowHttpError err))

        Ok msg ->
            msg


loadJob : AppJob Msg
loadJob =
    Job.init


jobToCmd : HostApi.HostApiConfig -> AppJob Msg -> Cmd Msg
jobToCmd host job =
    let
        coreCmd =
            coreJobToCmd host (Core.actionsToJob job.actions)

        cmd =
            Job.attempt toastOnError GotoRoute host job
    in
    Cmd.batch [ coreCmd, cmd ]


coreJobToCmd : HostApi.HostApiConfig -> CoreJob Core.Msg -> Cmd Msg
coreJobToCmd host job =
    let
        coreCmd =
            Core.jobToCmd host job
                |> Cmd.map CoreMsg

        --Trap the login event
        loginCmd =
            List.filter (\a -> a == Job.LoggedIn) job.actions
                |> List.head
                |> Maybe.map (\_ -> jobToCmd host loadJob)
                |> Maybe.withDefault Cmd.none
    in
    Cmd.batch [ coreCmd, loginCmd ]

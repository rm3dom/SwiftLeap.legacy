module Model exposing (Model, init)

import AppJob exposing (AppJob)
import Components.Me as Me exposing (Me)
import Components.Tenants as Tenants exposing (Tenants)
import Components.Updates as Updates exposing (Updates)
import Components.Users as Users exposing (Users)
import Core as Core exposing (Core)
import Job exposing (Job)
import Model.Repo
import Msg exposing (Msg)
import Navigation
import Random exposing (Seed, initialSeed)
import Routing as Routing exposing (Routing)
import Time exposing (Time)
import Types.Flags exposing (Flags)
import UrlParser as Url


type alias Model =
    { routing : Routing
    , seed : Seed
    , core : Core
    , fullScreen : Bool
    , repo : Model.Repo.Repo
    , tenants : Tenants
    , users : Users
    , updates : Updates
    , me : Me
    }


init : Flags -> Navigation.Location -> ( Model, AppJob Msg )
init flags location =
    let
        ( core, coreJob ) =
            Core.init flags location

        --What a mess
        maybeRoute =
            Url.parseHash Routing.parseRoute location

        job =
            coreJob
                |> Job.map Msg.CoreMsg
                |> Job.mapRoute Routing.NoRoute

        jobWithRoute =
            case maybeRoute of
                Nothing ->
                    job

                Just route ->
                    Job.addRoute route job
    in
    ( { routing = Routing.init location
      , seed = initialSeed 10
      , core = core
      , fullScreen = False
      , repo = Model.Repo.init
      , tenants = Tenants.init
      , users = Users.init
      , updates = Updates.init
      , me = Me.init
      }
    , jobWithRoute
    )

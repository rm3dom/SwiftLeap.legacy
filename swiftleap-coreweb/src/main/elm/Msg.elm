module Msg exposing (Msg(..))

import Components.Me as Me
import Components.Tenants as Tenants
import Components.Updates as Updates
import Components.Users as Users
import Core as Core
import Navigation
import Routing exposing (Route)
import Time exposing (Time)


type Msg
    = NoOp
    | Tick Time
    | CoreMsg Core.Msg
    | UrlChange Navigation.Location -- URL location changed
    | GotoRoute Route -- Go to some URL location
    | TenantsMsg Tenants.Msg
    | UpdatesMsg Updates.Msg
    | MeMsg Me.Msg
    | UsersMsg Users.Msg

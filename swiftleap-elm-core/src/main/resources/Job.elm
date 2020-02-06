module Job exposing (Action(..), CoreJob, HostToTask, Job, NoRoute(..), addAction, addApi, addCmd, addCmds, addMsg, addRoute, attempt, cmds, httpMap, init, map, mapAction, mapRoute, routes, toCmd, union)

import HostApi exposing (HostApiConfig)
import Http
import Task exposing (Task)


toCmd : msg -> Cmd msg
toCmd msg =
    Task.perform identity (Task.succeed msg)


type Action
    = ToastInfo String
    | ToastWarning String
    | ToastError String
    | ToastHide
    | LoaderShow String
    | LoaderHide
    | LoggedIn
    | ReloadRepo


type NoRoute
    = NoRoute


type alias CoreJob msg =
    Job msg NoRoute Action


type alias Job msg route action =
    { cmds : List (Cmd msg)
    , routes : List route
    , actions : List action
    , apiCalls : List (HostToTask msg)
    }


init : Job msg route action
init =
    { cmds = []
    , routes = []
    , actions = []
    , apiCalls = []
    }


{-| merge left with right. see unionWith
-}
union : Job msg route action -> Job msg route action -> Job msg route action
union left right =
    { cmds = right.cmds ++ left.cmds
    , routes = right.routes ++ left.routes
    , apiCalls = right.apiCalls ++ left.apiCalls
    , actions = right.actions ++ left.actions
    }


type alias HostToTask payload =
    HostApiConfig -> Task Http.Error payload


httpMap : (payload -> b) -> HostToTask payload -> HostToTask b
httpMap msg hostToTask =
    hostToTask >> Task.map msg


addAction : action -> Job msg route action -> Job msg route action
addAction action job =
    { job | actions = job.actions ++ [ action ] }


addApi : (payload -> msg) -> HostToTask payload -> Job msg route action -> Job msg route action
addApi tag httpTask job =
    { job | apiCalls = httpMap tag httpTask :: job.apiCalls }


addRoute : route -> Job msg route action -> Job msg route action
addRoute route job =
    { job | routes = job.routes ++ [ route ] }


addMsg : msg -> Job msg route action -> Job msg route action
addMsg msg job =
    { job | cmds = job.cmds ++ [ toCmd msg ] }


addCmd : Cmd msg -> Job msg route action -> Job msg route action
addCmd cmd job =
    { job | cmds = job.cmds ++ [ cmd ] }


addCmds : List (Cmd msg) -> Job msg route action -> Job msg route action
addCmds cmds job =
    { job | cmds = job.cmds ++ cmds }


cmds : Job msg route action -> List (Cmd msg)
cmds job =
    job.cmds


routes : Job msg route action -> List route
routes job =
    job.routes


map : (a -> b) -> Job a route action -> Job b route action
map mapper job =
    let
        newCmds =
            job.cmds
                |> List.map (Cmd.map mapper)

        apiCalls =
            List.map (httpMap mapper) job.apiCalls
    in
    { job | cmds = newCmds, apiCalls = apiCalls }


mapRoute : (a -> b) -> Job msg a action -> Job msg b action
mapRoute mapper job =
    { job | routes = List.map mapper job.routes }


mapAction : (a -> b) -> Job msg route a -> Job msg route b
mapAction mapper job =
    { job | actions = List.map mapper job.actions }


attempt : (Result Http.Error msg -> msg) -> (route -> msg) -> HostApiConfig -> Job msg route action -> Cmd msg
attempt onError routeMapper host job =
    let
        cmds =
            job.cmds

        routeCmds =
            job
                |> mapRoute (toCmd << routeMapper)
                |> routes

        apiTasksCmd =
            job.apiCalls
                |> List.map (\task -> task host)
                |> List.map (Task.attempt onError)
    in
    Cmd.batch (cmds ++ routeCmds ++ apiTasksCmd)

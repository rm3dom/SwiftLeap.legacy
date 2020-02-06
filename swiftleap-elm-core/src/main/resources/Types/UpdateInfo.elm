module Types.UpdateInfo exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.web.api.system.model.UpdateInfoDto -}

type UpdateInfoFields 
    = RestartRequired (Bool)
    | LatestVersion (String)
    | PatchedVersion (String)
    | OnLatest (Bool)
    | CurrentVersion (String)

type alias UpdateInfo =
    { restartRequired : Bool
    , latestVersion : String
    , patchedVersion : String
    , onLatest : Bool
    , currentVersion : String
    }

init: UpdateInfo
init = 
    { restartRequired = False
    , latestVersion = ""
    , patchedVersion = ""
    , onLatest = True
    , currentVersion = ""
    }

decode: JD.Decoder UpdateInfo
decode = 
    JDP.decode UpdateInfo
        |> JDP.optional "restartRequired" JD.bool False
        |> JDP.optional "latestVersion" JD.string ""
        |> JDP.optional "patchedVersion" JD.string ""
        |> JDP.optional "onLatest" JD.bool True
        |> JDP.optional "currentVersion" JD.string ""

encode: UpdateInfo -> JE.Value
encode o = 
    JE.object 
        [ ( "restartRequired", o.restartRequired |> JE.bool)
        , ( "latestVersion", o.latestVersion |> JE.string)
        , ( "patchedVersion", o.patchedVersion |> JE.string)
        , ( "onLatest", o.onLatest |> JE.bool)
        , ( "currentVersion", o.currentVersion |> JE.string)
        ]



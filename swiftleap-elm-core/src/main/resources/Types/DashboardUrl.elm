module Types.DashboardUrl exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.web.api.model.DashboardUrlDto -}

type DashboardUrlFields 
    = RefreshInterval (Int)
    | Index (Int)
    | ForceRefresh (Bool)
    | Url (String)

type alias DashboardUrl =
    { refreshInterval : Int
    , index : Int
    , forceRefresh : Bool
    , url : String
    }

init: DashboardUrl
init = 
    { refreshInterval = 0
    , index = 0
    , forceRefresh = False
    , url = ""
    }

decode: JD.Decoder DashboardUrl
decode = 
    JDP.decode DashboardUrl
        |> JDP.optional "refreshInterval" JD.int 0
        |> JDP.optional "index" JD.int 0
        |> JDP.optional "forceRefresh" JD.bool False
        |> JDP.optional "url" JD.string ""

encode: DashboardUrl -> JE.Value
encode o = 
    JE.object 
        [ ( "refreshInterval", o.refreshInterval |> JE.int)
        , ( "index", o.index |> JE.int)
        , ( "forceRefresh", o.forceRefresh |> JE.bool)
        , ( "url", o.url |> JE.string)
        ]



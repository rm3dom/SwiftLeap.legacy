module Types.Dashboard exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Types.DashboardUrl as DashboardUrl


{- Generated from org.swiftleap.zabbix.web.api.model.DashboardDto -}

type DashboardFields 
    = Urls (List DashboardUrl.DashboardUrl)
    | Name (String)

type alias Dashboard =
    { urls : List DashboardUrl.DashboardUrl
    , name : String
    }

init: Dashboard
init = 
    { urls = []
    , name = ""
    }

decode: JD.Decoder Dashboard
decode = 
    JDP.decode Dashboard
        |> JDP.required "urls" (JD.list DashboardUrl.decode)
        |> JDP.optional "name" JD.string ""

encode: Dashboard -> JE.Value
encode o = 
    JE.object 
        [ ( "urls", o.urls |> List.map DashboardUrl.encode |> JE.list)
        , ( "name", o.name |> JE.string)
        ]



module Types.Report exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.web.api.model.ReportDto -}

type ReportFields 
    = Id (String)
    | Status (String)

type alias Report =
    { id : String
    , status : String
    }

init: Report
init = 
    { id = ""
    , status = ""
    }

decode: JD.Decoder Report
decode = 
    JDP.decode Report
        |> JDP.optional "id" JD.string ""
        |> JDP.optional "status" JD.string ""

encode: Report -> JE.Value
encode o = 
    JE.object 
        [ ( "id", o.id |> JE.string)
        , ( "status", o.status |> JE.string)
        ]



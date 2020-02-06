module Types.BasicHost exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.web.api.model.BasicHostDto -}

type BasicHostFields 
    = HostName (String)
    | HostId (Int)

type alias BasicHost =
    { hostName : String
    , hostId : Int
    }

init: BasicHost
init = 
    { hostName = ""
    , hostId = 0
    }

decode: JD.Decoder BasicHost
decode = 
    JDP.decode BasicHost
        |> JDP.optional "hostName" JD.string ""
        |> JDP.optional "hostId" JD.int 0

encode: BasicHost -> JE.Value
encode o = 
    JE.object 
        [ ( "hostName", o.hostName |> JE.string)
        , ( "hostId", o.hostId |> JE.int)
        ]



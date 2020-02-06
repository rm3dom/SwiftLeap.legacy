module Types.TemplateHost exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.zabbix.web.api.model.TemplateHostDto -}

type TemplateHostFields 
    = Interfaces (List String)
    | HostId (Int)

type alias TemplateHost =
    { interfaces : List String
    , hostId : Int
    }

init: TemplateHost
init = 
    { interfaces = []
    , hostId = 0
    }

decode: JD.Decoder TemplateHost
decode = 
    JDP.decode TemplateHost
        |> JDP.required "interfaces" (JD.list JD.string)
        |> JDP.optional "hostId" JD.int 0

encode: TemplateHost -> JE.Value
encode o = 
    JE.object 
        [ ( "interfaces", o.interfaces |> List.map JE.string |> JE.list)
        , ( "hostId", o.hostId |> JE.int)
        ]



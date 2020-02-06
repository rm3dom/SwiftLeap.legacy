module Types.Template exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Types.TemplateHost as TemplateHost


{- Generated from org.swiftleap.zabbix.web.api.model.TemplateDto -}

type TemplateFields 
    = Month (Int)
    | Year (Int)
    | Hosts (List TemplateHost.TemplateHost)
    | Name (String)

type alias Template =
    { month : Int
    , year : Int
    , hosts : List TemplateHost.TemplateHost
    , name : String
    }

init: Template
init = 
    { month = 0
    , year = 0
    , hosts = []
    , name = ""
    }

decode: JD.Decoder Template
decode = 
    JDP.decode Template
        |> JDP.optional "month" JD.int 0
        |> JDP.optional "year" JD.int 0
        |> JDP.required "hosts" (JD.list TemplateHost.decode)
        |> JDP.optional "name" JD.string ""

encode: Template -> JE.Value
encode o = 
    JE.object 
        [ ( "month", o.month |> JE.int)
        , ( "year", o.year |> JE.int)
        , ( "hosts", o.hosts |> List.map TemplateHost.encode |> JE.list)
        , ( "name", o.name |> JE.string)
        ]



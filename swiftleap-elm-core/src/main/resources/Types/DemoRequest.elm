module Types.DemoRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.DemoRequestDto -}

type DemoRequestFields 
    = TenantId (Int)

type alias DemoRequest =
    { tenantId : Int
    }

init: DemoRequest
init = 
    { tenantId = 0
    }

decode: JD.Decoder DemoRequest
decode = 
    JDP.decode DemoRequest
        |> JDP.optional "tenantId" JD.int 0

encode: DemoRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "tenantId", o.tenantId |> JE.int)
        ]



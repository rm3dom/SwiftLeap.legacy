module Types.DemoResponse exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.DemoResponseDto -}

type DemoResponseFields 
    = TenantId (Int)

type alias DemoResponse =
    { tenantId : Int
    }

init: DemoResponse
init = 
    { tenantId = 0
    }

decode: JD.Decoder DemoResponse
decode = 
    JDP.decode DemoResponse
        |> JDP.optional "tenantId" JD.int 0

encode: DemoResponse -> JE.Value
encode o = 
    JE.object 
        [ ( "tenantId", o.tenantId |> JE.int)
        ]



module Types.NewVersionRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.NewVersionRequestDto -}

type NewVersionRequestFields 
    = BaseVersionId (Int)
    | Description (String)

type alias NewVersionRequest =
    { baseVersionId : Int
    , description : String
    }

init: NewVersionRequest
init = 
    { baseVersionId = 0
    , description = ""
    }

decode: JD.Decoder NewVersionRequest
decode = 
    JDP.decode NewVersionRequest
        |> JDP.optional "baseVersionId" JD.int 0
        |> JDP.optional "description" JD.string ""

encode: NewVersionRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "baseVersionId", o.baseVersionId |> JE.int)
        , ( "description", o.description |> JE.string)
        ]



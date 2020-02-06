module Types.Pair exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.util.dto.PairDto -}

type PairFields 
    = Value2 (String)
    | Value1 (String)

type alias Pair =
    { value2 : String
    , value1 : String
    }

init: Pair
init = 
    { value2 = ""
    , value1 = ""
    }

decode: JD.Decoder Pair
decode = 
    JDP.decode Pair
        |> JDP.optional "value2" JD.string ""
        |> JDP.optional "value1" JD.string ""

encode: Pair -> JE.Value
encode o = 
    JE.object 
        [ ( "value2", o.value2 |> JE.string)
        , ( "value1", o.value1 |> JE.string)
        ]



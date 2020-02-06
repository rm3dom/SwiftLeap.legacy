module Types.Error exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.web.ErrorDTO -}

type ErrorFields 
    = Reference (String)
    | Code (Int)
    | Message (String)

type alias Error =
    { reference : String
    , code : Int
    , message : String
    }

init: Error
init = 
    { reference = ""
    , code = 0
    , message = ""
    }

decode: JD.Decoder Error
decode = 
    JDP.decode Error
        |> JDP.optional "reference" JD.string ""
        |> JDP.optional "code" JD.int 0
        |> JDP.optional "message" JD.string ""

encode: Error -> JE.Value
encode o = 
    JE.object 
        [ ( "reference", o.reference |> JE.string)
        , ( "code", o.code |> JE.int)
        , ( "message", o.message |> JE.string)
        ]



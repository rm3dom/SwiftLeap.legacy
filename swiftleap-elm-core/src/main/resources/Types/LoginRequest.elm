module Types.LoginRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.ecom.LoginRequest -}

type LoginRequestFields 
    = Mobile (String)
    | Email (String)

type alias LoginRequest =
    { mobile : String
    , email : String
    }

init: LoginRequest
init = 
    { mobile = ""
    , email = ""
    }

decode: JD.Decoder LoginRequest
decode = 
    JDP.decode LoginRequest
        |> JDP.optional "mobile" JD.string ""
        |> JDP.optional "email" JD.string ""

encode: LoginRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "mobile", o.mobile |> JE.string)
        , ( "email", o.email |> JE.string)
        ]



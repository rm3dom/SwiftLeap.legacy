module Types.AuthRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.web.api.system.model.AuthRequestDto -}

type AuthRequestFields 
    = Password (String)
    | UserName (String)

type alias AuthRequest =
    { password : String
    , userName : String
    }

init: AuthRequest
init = 
    { password = ""
    , userName = ""
    }

decode: JD.Decoder AuthRequest
decode = 
    JDP.decode AuthRequest
        |> JDP.optional "password" JD.string ""
        |> JDP.optional "userName" JD.string ""

encode: AuthRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "password", o.password |> JE.string)
        , ( "userName", o.userName |> JE.string)
        ]



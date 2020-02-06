module Types.Client exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.seed.web.api.mobile.model.ClientDto -}

type ClientFields 
    = FirstName (String)
    | ClientId (String)
    | Code (String)
    | Gender (String)
    | Surname (String)
    | Mobile (String)
    | Id (Int)
    | Email (String)
    | Points (Int)

type alias Client =
    { firstName : String
    , clientId : String
    , code : String
    , gender : String
    , surname : String
    , mobile : String
    , id : Int
    , email : String
    , points : Int
    }

init: Client
init = 
    { firstName = ""
    , clientId = ""
    , code = ""
    , gender = ""
    , surname = ""
    , mobile = ""
    , id = 0
    , email = ""
    , points = 0
    }

decode: JD.Decoder Client
decode = 
    JDP.decode Client
        |> JDP.optional "firstName" JD.string ""
        |> JDP.optional "clientId" JD.string ""
        |> JDP.optional "code" JD.string ""
        |> JDP.optional "gender" JD.string ""
        |> JDP.optional "surname" JD.string ""
        |> JDP.optional "mobile" JD.string ""
        |> JDP.optional "id" JD.int 0
        |> JDP.optional "email" JD.string ""
        |> JDP.optional "points" JD.int 0

encode: Client -> JE.Value
encode o = 
    JE.object 
        [ ( "firstName", o.firstName |> JE.string)
        , ( "clientId", o.clientId |> JE.string)
        , ( "code", o.code |> JE.string)
        , ( "gender", o.gender |> JE.string)
        , ( "surname", o.surname |> JE.string)
        , ( "mobile", o.mobile |> JE.string)
        , ( "id", o.id |> JE.int)
        , ( "email", o.email |> JE.string)
        , ( "points", o.points |> JE.int)
        ]



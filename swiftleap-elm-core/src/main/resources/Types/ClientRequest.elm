module Types.ClientRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.ecom.ClientRequest -}

type ClientRequestFields 
    = FirstName (String)
    | Code (String)
    | Gender (String)
    | CountryCode (String)
    | Surname (String)
    | Mobile (String)
    | Id (Int)
    | Email (String)

type alias ClientRequest =
    { firstName : String
    , code : String
    , gender : String
    , countryCode : String
    , surname : String
    , mobile : String
    , id : Int
    , email : String
    }

init: ClientRequest
init = 
    { firstName = ""
    , code = ""
    , gender = ""
    , countryCode = ""
    , surname = ""
    , mobile = ""
    , id = 0
    , email = ""
    }

decode: JD.Decoder ClientRequest
decode = 
    JDP.decode ClientRequest
        |> JDP.optional "firstName" JD.string ""
        |> JDP.optional "code" JD.string ""
        |> JDP.optional "gender" JD.string ""
        |> JDP.optional "countryCode" JD.string ""
        |> JDP.optional "surname" JD.string ""
        |> JDP.optional "mobile" JD.string ""
        |> JDP.optional "id" JD.int 0
        |> JDP.optional "email" JD.string ""

encode: ClientRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "firstName", o.firstName |> JE.string)
        , ( "code", o.code |> JE.string)
        , ( "gender", o.gender |> JE.string)
        , ( "countryCode", o.countryCode |> JE.string)
        , ( "surname", o.surname |> JE.string)
        , ( "mobile", o.mobile |> JE.string)
        , ( "id", o.id |> JE.int)
        , ( "email", o.email |> JE.string)
        ]



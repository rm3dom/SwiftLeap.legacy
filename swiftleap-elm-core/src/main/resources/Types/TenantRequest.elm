module Types.TenantRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.security.TenantRequest -}

type TenantRequestFields 
    = Password (String)
    | Fqdn (String)
    | CountryCode (String)
    | Name (String)
    | Id (Maybe Int)
    | PartyId (Int)
    | UserName (String)
    | Email (String)
    | Activated (Bool)
    | LongName (String)

type alias TenantRequest =
    { password : String
    , fqdn : String
    , countryCode : String
    , name : String
    , id : Maybe Int
    , partyId : Int
    , userName : String
    , email : String
    , activated : Bool
    , longName : String
    }

init: TenantRequest
init = 
    { password = ""
    , fqdn = ""
    , countryCode = ""
    , name = ""
    , id = Nothing
    , partyId = 0
    , userName = ""
    , email = ""
    , activated = True
    , longName = ""
    }

decode: JD.Decoder TenantRequest
decode = 
    JDP.decode TenantRequest
        |> JDP.optional "password" JD.string ""
        |> JDP.optional "fqdn" JD.string ""
        |> JDP.optional "countryCode" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.required "id" (JD.nullable JD.int)
        |> JDP.optional "partyId" JD.int 0
        |> JDP.optional "userName" JD.string ""
        |> JDP.optional "email" JD.string ""
        |> JDP.optional "activated" JD.bool True
        |> JDP.optional "longName" JD.string ""

encode: TenantRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "password", o.password |> JE.string)
        , ( "fqdn", o.fqdn |> JE.string)
        , ( "countryCode", o.countryCode |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "id", o.id |> encodeMaybe JE.int)
        , ( "partyId", o.partyId |> JE.int)
        , ( "userName", o.userName |> JE.string)
        , ( "email", o.email |> JE.string)
        , ( "activated", o.activated |> JE.bool)
        , ( "longName", o.longName |> JE.string)
        ]



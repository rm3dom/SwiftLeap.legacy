module Types.UserRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.security.UserRequest -}

type UserRequestFields 
    = FirstName (String)
    | Password (String)
    | Surname (String)
    | Roles (List String)
    | TenantId (Maybe Int)
    | Description (String)
    | Id (Maybe Int)
    | PartyId (Maybe Int)
    | UserName (String)
    | Email (String)
    | Activated (Bool)

type alias UserRequest =
    { firstName : String
    , password : String
    , surname : String
    , roles : List String
    , tenantId : Maybe Int
    , description : String
    , id : Maybe Int
    , partyId : Maybe Int
    , userName : String
    , email : String
    , activated : Bool
    }

init: UserRequest
init = 
    { firstName = ""
    , password = ""
    , surname = ""
    , roles = []
    , tenantId = Nothing
    , description = ""
    , id = Nothing
    , partyId = Nothing
    , userName = ""
    , email = ""
    , activated = True
    }

decode: JD.Decoder UserRequest
decode = 
    JDP.decode UserRequest
        |> JDP.optional "firstName" JD.string ""
        |> JDP.optional "password" JD.string ""
        |> JDP.optional "surname" JD.string ""
        |> JDP.optional "roles" (JD.list JD.string) []
        |> JDP.required "tenantId" (JD.nullable JD.int)
        |> JDP.optional "description" JD.string ""
        |> JDP.required "id" (JD.nullable JD.int)
        |> JDP.required "partyId" (JD.nullable JD.int)
        |> JDP.optional "userName" JD.string ""
        |> JDP.optional "email" JD.string ""
        |> JDP.optional "activated" JD.bool True

encode: UserRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "firstName", o.firstName |> JE.string)
        , ( "password", o.password |> JE.string)
        , ( "surname", o.surname |> JE.string)
        , ( "roles", o.roles |> List.map JE.string |> JE.list)
        , ( "tenantId", o.tenantId |> encodeMaybe JE.int)
        , ( "description", o.description |> JE.string)
        , ( "id", o.id |> encodeMaybe JE.int)
        , ( "partyId", o.partyId |> encodeMaybe JE.int)
        , ( "userName", o.userName |> JE.string)
        , ( "email", o.email |> JE.string)
        , ( "activated", o.activated |> JE.bool)
        ]



module Types.User exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.security.dto.UserDto -}

type UserFields 
    = FirstName (String)
    | Image (String)
    | Surname (String)
    | Roles (List String)
    | Name (String)
    | TenantId (Maybe Int)
    | ImageMime (String)
    | Id (Maybe Int)
    | SessionId (String)
    | UserName (String)
    | Email (String)
    | Activated (Bool)
    | Managed (Bool)

type alias User =
    { firstName : String
    , image : String
    , surname : String
    , roles : List String
    , name : String
    , tenantId : Maybe Int
    , imageMime : String
    , id : Maybe Int
    , sessionId : String
    , userName : String
    , email : String
    , activated : Bool
    , managed : Bool
    }

init: User
init = 
    { firstName = ""
    , image = ""
    , surname = ""
    , roles = []
    , name = ""
    , tenantId = Nothing
    , imageMime = ""
    , id = Nothing
    , sessionId = ""
    , userName = ""
    , email = ""
    , activated = False
    , managed = True
    }

decode: JD.Decoder User
decode = 
    JDP.decode User
        |> JDP.optional "firstName" JD.string ""
        |> JDP.optional "image" JD.string ""
        |> JDP.optional "surname" JD.string ""
        |> JDP.optional "roles" (JD.list JD.string) []
        |> JDP.optional "name" JD.string ""
        |> JDP.required "tenantId" (JD.nullable JD.int)
        |> JDP.optional "imageMime" JD.string ""
        |> JDP.required "id" (JD.nullable JD.int)
        |> JDP.optional "sessionId" JD.string ""
        |> JDP.optional "userName" JD.string ""
        |> JDP.optional "email" JD.string ""
        |> JDP.optional "activated" JD.bool False
        |> JDP.optional "managed" JD.bool True

encode: User -> JE.Value
encode o = 
    JE.object 
        [ ( "firstName", o.firstName |> JE.string)
        , ( "image", o.image |> JE.string)
        , ( "surname", o.surname |> JE.string)
        , ( "roles", o.roles |> List.map JE.string |> JE.list)
        , ( "name", o.name |> JE.string)
        , ( "tenantId", o.tenantId |> encodeMaybe JE.int)
        , ( "imageMime", o.imageMime |> JE.string)
        , ( "id", o.id |> encodeMaybe JE.int)
        , ( "sessionId", o.sessionId |> JE.string)
        , ( "userName", o.userName |> JE.string)
        , ( "email", o.email |> JE.string)
        , ( "activated", o.activated |> JE.bool)
        , ( "managed", o.activated |> JE.bool)
        ]



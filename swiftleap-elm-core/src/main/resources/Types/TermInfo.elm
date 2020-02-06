module Types.TermInfo exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.TermInfoDto -}

type TermInfoFields 
    = LastUpdatedBy (String)
    | CreationTime (DateTime)
    | CreatedBy (String)
    | GroupId (Int)
    | Name (String)
    | Description (String)
    | Language (String)
    | Id (String)
    | Url (String)
    | LastUpdateTime (DateTime)

type alias TermInfo =
    { lastUpdatedBy : String
    , creationTime : DateTime
    , createdBy : String
    , groupId : Int
    , name : String
    , description : String
    , language : String
    , id : String
    , url : String
    , lastUpdateTime : DateTime
    }

init: TermInfo
init = 
    { lastUpdatedBy = ""
    , creationTime = initDateTime
    , createdBy = ""
    , groupId = 0
    , name = ""
    , description = ""
    , language = ""
    , id = ""
    , url = ""
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder TermInfo
decode = 
    JDP.decode TermInfo
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "groupId" JD.int 0
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "language" JD.string ""
        |> JDP.optional "id" JD.string ""
        |> JDP.optional "url" JD.string ""
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: TermInfo -> JE.Value
encode o = 
    JE.object 
        [ ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "groupId", o.groupId |> JE.int)
        , ( "name", o.name |> JE.string)
        , ( "description", o.description |> JE.string)
        , ( "language", o.language |> JE.string)
        , ( "id", o.id |> JE.string)
        , ( "url", o.url |> JE.string)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]



module Types.Term exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.TermDto -}

type TermFields 
    = LastUpdatedBy (String)
    | CreationTime (DateTime)
    | Documentation (String)
    | GroupId (Int)
    | HtmlCode (String)
    | Usedin (List String)
    | Description (String)
    | Language (String)
    | Url (String)
    | CreatedBy (String)
    | Name (String)
    | Id (String)
    | LastUpdateTime (DateTime)

type alias Term =
    { lastUpdatedBy : String
    , creationTime : DateTime
    , documentation : String
    , groupId : Int
    , htmlCode : String
    , usedin : List String
    , description : String
    , language : String
    , url : String
    , createdBy : String
    , name : String
    , id : String
    , lastUpdateTime : DateTime
    }

init: Term
init = 
    { lastUpdatedBy = ""
    , creationTime = initDateTime
    , documentation = ""
    , groupId = 0
    , htmlCode = ""
    , usedin = []
    , description = ""
    , language = ""
    , url = ""
    , createdBy = ""
    , name = ""
    , id = ""
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder Term
decode = 
    JDP.decode Term
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "documentation" JD.string ""
        |> JDP.optional "groupId" JD.int 0
        |> JDP.optional "htmlCode" JD.string ""
        |> JDP.optional "usedin" (JD.list JD.string) []
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "language" JD.string ""
        |> JDP.optional "url" JD.string ""
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "id" JD.string ""
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: Term -> JE.Value
encode o = 
    JE.object 
        [ ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "documentation", o.documentation |> JE.string)
        , ( "groupId", o.groupId |> JE.int)
        , ( "htmlCode", o.htmlCode |> JE.string)
        , ( "usedin", o.usedin |> List.map JE.string |> JE.list)
        , ( "description", o.description |> JE.string)
        , ( "language", o.language |> JE.string)
        , ( "url", o.url |> JE.string)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "id", o.id |> JE.string)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]



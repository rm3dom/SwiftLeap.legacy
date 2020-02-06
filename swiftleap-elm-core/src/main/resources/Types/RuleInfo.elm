module Types.RuleInfo exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.RuleInfoDto -}

type RuleInfoFields 
    = Severity (Int)
    | Inverse (Bool)
    | LastUpdatedBy (String)
    | CreationTime (DateTime)
    | GroupId (Int)
    | Description (String)
    | Language (String)
    | MappedCode (String)
    | Message (String)
    | Enabled (Bool)
    | Url (String)
    | RuleCode (String)
    | CreatedBy (String)
    | Name (String)
    | Id (String)
    | LastUpdateTime (DateTime)

type alias RuleInfo =
    { severity : Int
    , inverse : Bool
    , lastUpdatedBy : String
    , creationTime : DateTime
    , groupId : Int
    , description : String
    , language : String
    , mappedCode : String
    , message : String
    , enabled : Bool
    , url : String
    , ruleCode : String
    , createdBy : String
    , name : String
    , id : String
    , lastUpdateTime : DateTime
    }

init: RuleInfo
init = 
    { severity = 0
    , inverse = False
    , lastUpdatedBy = ""
    , creationTime = initDateTime
    , groupId = 0
    , description = ""
    , language = ""
    , mappedCode = ""
    , message = ""
    , enabled = False
    , url = ""
    , ruleCode = ""
    , createdBy = ""
    , name = ""
    , id = ""
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder RuleInfo
decode = 
    JDP.decode RuleInfo
        |> JDP.optional "severity" JD.int 0
        |> JDP.optional "inverse" JD.bool False
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "groupId" JD.int 0
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "language" JD.string ""
        |> JDP.optional "mappedCode" JD.string ""
        |> JDP.optional "message" JD.string ""
        |> JDP.optional "enabled" JD.bool False
        |> JDP.optional "url" JD.string ""
        |> JDP.optional "ruleCode" JD.string ""
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "id" JD.string ""
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: RuleInfo -> JE.Value
encode o = 
    JE.object 
        [ ( "severity", o.severity |> JE.int)
        , ( "inverse", o.inverse |> JE.bool)
        , ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "groupId", o.groupId |> JE.int)
        , ( "description", o.description |> JE.string)
        , ( "language", o.language |> JE.string)
        , ( "mappedCode", o.mappedCode |> JE.string)
        , ( "message", o.message |> JE.string)
        , ( "enabled", o.enabled |> JE.bool)
        , ( "url", o.url |> JE.string)
        , ( "ruleCode", o.ruleCode |> JE.string)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "id", o.id |> JE.string)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]



module Types.Rule exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.RuleDto -}

type RuleFields 
    = Severity (Int)
    | Inverse (Bool)
    | LastUpdatedBy (String)
    | CreationTime (DateTime)
    | Documentation (String)
    | GroupId (Int)
    | HtmlCode (String)
    | Usedin (List String)
    | Description (String)
    | Language (String)
    | MappedCode (String)
    | Message (String)
    | Priority (Int)
    | Enabled (Bool)
    | LastTestedTime (DateTime)
    | Url (String)
    | RuleCode (String)
    | CreatedBy (String)
    | Name (String)
    | TestId (Int)
    | TestSuccessful (Bool)
    | Id (String)
    | LastTestedBy (String)
    | LastUpdateTime (DateTime)

type alias Rule =
    { severity : Int
    , inverse : Bool
    , lastUpdatedBy : String
    , creationTime : DateTime
    , documentation : String
    , groupId : Int
    , htmlCode : String
    , usedin : List String
    , description : String
    , language : String
    , mappedCode : String
    , message : String
    , priority : Int
    , enabled : Bool
    , lastTestedTime : DateTime
    , url : String
    , ruleCode : String
    , createdBy : String
    , name : String
    , testId : Int
    , testSuccessful : Bool
    , id : String
    , lastTestedBy : String
    , lastUpdateTime : DateTime
    }

init: Rule
init = 
    { severity = 0
    , inverse = False
    , lastUpdatedBy = ""
    , creationTime = initDateTime
    , documentation = ""
    , groupId = 0
    , htmlCode = ""
    , usedin = []
    , description = ""
    , language = ""
    , mappedCode = ""
    , message = ""
    , priority = 0
    , enabled = True
    , lastTestedTime = initDateTime
    , url = ""
    , ruleCode = ""
    , createdBy = ""
    , name = ""
    , testId = 0
    , testSuccessful = False
    , id = ""
    , lastTestedBy = ""
    , lastUpdateTime = initDateTime
    }

decode: JD.Decoder Rule
decode = 
    JDP.decode Rule
        |> JDP.optional "severity" JD.int 0
        |> JDP.optional "inverse" JD.bool False
        |> JDP.optional "lastUpdatedBy" JD.string ""
        |> JDP.optional "creationTime" dateTimeDecoder initDateTime
        |> JDP.optional "documentation" JD.string ""
        |> JDP.optional "groupId" JD.int 0
        |> JDP.optional "htmlCode" JD.string ""
        |> JDP.optional "usedin" (JD.list JD.string) []
        |> JDP.optional "description" JD.string ""
        |> JDP.optional "language" JD.string ""
        |> JDP.optional "mappedCode" JD.string ""
        |> JDP.optional "message" JD.string ""
        |> JDP.optional "priority" JD.int 0
        |> JDP.optional "enabled" JD.bool True
        |> JDP.optional "lastTestedTime" dateTimeDecoder initDateTime
        |> JDP.optional "url" JD.string ""
        |> JDP.optional "ruleCode" JD.string ""
        |> JDP.optional "createdBy" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "testId" JD.int 0
        |> JDP.optional "testSuccessful" JD.bool False
        |> JDP.optional "id" JD.string ""
        |> JDP.optional "lastTestedBy" JD.string ""
        |> JDP.optional "lastUpdateTime" dateTimeDecoder initDateTime

encode: Rule -> JE.Value
encode o = 
    JE.object 
        [ ( "severity", o.severity |> JE.int)
        , ( "inverse", o.inverse |> JE.bool)
        , ( "lastUpdatedBy", o.lastUpdatedBy |> JE.string)
        , ( "creationTime", o.creationTime |> dateTimeEncoder)
        , ( "documentation", o.documentation |> JE.string)
        , ( "groupId", o.groupId |> JE.int)
        , ( "htmlCode", o.htmlCode |> JE.string)
        , ( "usedin", o.usedin |> List.map JE.string |> JE.list)
        , ( "description", o.description |> JE.string)
        , ( "language", o.language |> JE.string)
        , ( "mappedCode", o.mappedCode |> JE.string)
        , ( "message", o.message |> JE.string)
        , ( "priority", o.priority |> JE.int)
        , ( "enabled", o.enabled |> JE.bool)
        , ( "lastTestedTime", o.lastTestedTime |> dateTimeEncoder)
        , ( "url", o.url |> JE.string)
        , ( "ruleCode", o.ruleCode |> JE.string)
        , ( "createdBy", o.createdBy |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "testId", o.testId |> JE.int)
        , ( "testSuccessful", o.testSuccessful |> JE.bool)
        , ( "id", o.id |> JE.string)
        , ( "lastTestedBy", o.lastTestedBy |> JE.string)
        , ( "lastUpdateTime", o.lastUpdateTime |> dateTimeEncoder)
        ]



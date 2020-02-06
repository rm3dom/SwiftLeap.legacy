module Types.TestResult exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.TestResultDto -}

type TestResultFields 
    = RuleCode (String)
    | GroupId (Int)
    | Name (String)
    | Messages (List String)
    | TestId (Int)
    | Message (String)
    | RuleId (String)
    | Status (String)
    | TestName (String)

type alias TestResult =
    { ruleCode : String
    , groupId : Int
    , name : String
    , messages : List String
    , testId : Int
    , message : String
    , ruleId : String
    , status : String
    , testName : String
    }

init: TestResult
init = 
    { ruleCode = ""
    , groupId = 0
    , name = ""
    , messages = []
    , testId = 0
    , message = ""
    , ruleId = ""
    , status = ""
    , testName = ""
    }

decode: JD.Decoder TestResult
decode = 
    JDP.decode TestResult
        |> JDP.optional "ruleCode" JD.string ""
        |> JDP.optional "groupId" JD.int 0
        |> JDP.optional "name" JD.string ""
        |> JDP.optional "messages" (JD.list JD.string) []
        |> JDP.optional "testId" JD.int 0
        |> JDP.optional "message" JD.string ""
        |> JDP.optional "ruleId" JD.string ""
        |> JDP.optional "status" JD.string ""
        |> JDP.optional "testName" JD.string ""

encode: TestResult -> JE.Value
encode o = 
    JE.object 
        [ ( "ruleCode", o.ruleCode |> JE.string)
        , ( "groupId", o.groupId |> JE.int)
        , ( "name", o.name |> JE.string)
        , ( "messages", o.messages |> List.map JE.string |> JE.list)
        , ( "testId", o.testId |> JE.int)
        , ( "message", o.message |> JE.string)
        , ( "ruleId", o.ruleId |> JE.string)
        , ( "status", o.status |> JE.string)
        , ( "testName", o.testName |> JE.string)
        ]



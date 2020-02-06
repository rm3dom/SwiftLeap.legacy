module Types.TestResults exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.TestResult as TestResult


{- Generated from org.swiftleap.rules.web.api.model.TestResultsDto -}

type TestResultsFields 
    = Running (Bool)
    | Started (DateTime)
    | Completed (DateTime)
    | Failed (Int)
    | RunId (String)
    | RunningTime (String)
    | Results (List TestResult.TestResult)
    | Errors (Int)
    | NotRun (Int)
    | Succeeded (Int)

type alias TestResults =
    { running : Bool
    , started : DateTime
    , completed : DateTime
    , failed : Int
    , runId : String
    , runningTime : String
    , results : List TestResult.TestResult
    , errors : Int
    , notRun : Int
    , succeeded : Int
    }

init: TestResults
init = 
    { running = False
    , started = initDateTime
    , completed = initDateTime
    , failed = 0
    , runId = ""
    , runningTime = ""
    , results = []
    , errors = 0
    , notRun = 0
    , succeeded = 0
    }

decode: JD.Decoder TestResults
decode = 
    JDP.decode TestResults
        |> JDP.optional "running" JD.bool False
        |> JDP.optional "started" dateTimeDecoder initDateTime
        |> JDP.optional "completed" dateTimeDecoder initDateTime
        |> JDP.optional "failed" JD.int 0
        |> JDP.optional "runId" JD.string ""
        |> JDP.optional "runningTime" JD.string ""
        |> JDP.optional "results" (JD.list TestResult.decode) []
        |> JDP.optional "errors" JD.int 0
        |> JDP.optional "notRun" JD.int 0
        |> JDP.optional "succeeded" JD.int 0

encode: TestResults -> JE.Value
encode o = 
    JE.object 
        [ ( "running", o.running |> JE.bool)
        , ( "started", o.started |> dateTimeEncoder)
        , ( "completed", o.completed |> dateTimeEncoder)
        , ( "failed", o.failed |> JE.int)
        , ( "runId", o.runId |> JE.string)
        , ( "runningTime", o.runningTime |> JE.string)
        , ( "results", o.results |> List.map TestResult.encode |> JE.list)
        , ( "errors", o.errors |> JE.int)
        , ( "notRun", o.notRun |> JE.int)
        , ( "succeeded", o.succeeded |> JE.int)
        ]



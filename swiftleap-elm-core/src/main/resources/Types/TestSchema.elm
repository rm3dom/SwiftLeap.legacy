module Types.TestSchema exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)
import Types.TestData as TestData


{- Generated from org.swiftleap.rules.web.api.model.TestSchemaDto -}

type TestSchemaFields 
    = Columns (List String)
    | DataSetName (String)
    | Rows (List TestData.TestData)

type alias TestSchema =
    { columns : List String
    , dataSetName : String
    , rows : List TestData.TestData
    }

init: TestSchema
init = 
    { columns = []
    , dataSetName = ""
    , rows = []
    }

decode: JD.Decoder TestSchema
decode = 
    JDP.decode TestSchema
        |> JDP.optional "columns" (JD.list JD.string) []
        |> JDP.optional "dataSetName" JD.string ""
        |> JDP.optional "rows" (JD.list TestData.decode) []

encode: TestSchema -> JE.Value
encode o = 
    JE.object 
        [ ( "columns", o.columns |> List.map JE.string |> JE.list)
        , ( "dataSetName", o.dataSetName |> JE.string)
        , ( "rows", o.rows |> List.map TestData.encode |> JE.list)
        ]



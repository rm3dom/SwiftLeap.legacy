module Types.SaleRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)


{- Generated from org.swiftleap.ecom.SaleRequest -}

type SaleRequestFields 
    = VoucherNo (String)
    | Note (String)
    | Amount (Int)
    | ClientCode (String)
    | StockId (String)
    | Points (Int)

type alias SaleRequest =
    { voucherNo : String
    , note : String
    , amount : Int
    , clientCode : String
    , stockId : String
    , points : Int
    }

init: SaleRequest
init = 
    { voucherNo = ""
    , note = ""
    , amount = 0
    , clientCode = ""
    , stockId = ""
    , points = 0
    }

decode: JD.Decoder SaleRequest
decode = 
    JDP.decode SaleRequest
        |> JDP.optional "voucherNo" JD.string ""
        |> JDP.optional "note" JD.string ""
        |> JDP.optional "amount" JD.int 0
        |> JDP.optional "clientCode" JD.string ""
        |> JDP.optional "stockId" JD.string ""
        |> JDP.optional "points" JD.int 0

encode: SaleRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "voucherNo", o.voucherNo |> JE.string)
        , ( "note", o.note |> JE.string)
        , ( "amount", o.amount |> JE.int)
        , ( "clientCode", o.clientCode |> JE.string)
        , ( "stockId", o.stockId |> JE.string)
        , ( "points", o.points |> JE.int)
        ]



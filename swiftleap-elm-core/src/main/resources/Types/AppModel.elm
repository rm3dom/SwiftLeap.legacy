module Types.AppModel exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Types.Client as Client
import Types.Voucher as Voucher


{- Generated from org.swiftleap.seed.web.api.mobile.model.AppModelDto -}

type AppModelFields 
    = Client (Maybe Client.Client)
    | Vouchers (List Voucher.Voucher)

type alias AppModel =
    { client : Maybe Client.Client
    , vouchers : List Voucher.Voucher
    }

init: AppModel
init = 
    { client = Nothing
    , vouchers = []
    }

decode: JD.Decoder AppModel
decode = 
    JDP.decode AppModel
        |> JDP.required "client" (JD.nullable Client.decode)
        |> JDP.required "vouchers" (JD.list Voucher.decode)

encode: AppModel -> JE.Value
encode o = 
    JE.object 
        [ ( "client", o.client |> encodeMaybe Client.encode)
        , ( "vouchers", o.vouchers |> List.map Voucher.encode |> JE.list)
        ]



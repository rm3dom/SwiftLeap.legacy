module Types.Tenant exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.common.web.api.system.model.TenantDto -}

type TenantFields 
    = Fqdn (String)
    | CountryCode (String)
    | Name (String)
    | TenantId (Maybe Int)
    | PartyId (Int)
    | Activated (Bool)

type alias Tenant =
    { fqdn : String
    , countryCode : String
    , name : String
    , tenantId : Maybe Int
    , partyId : Int
    , activated : Bool
    }

init: Tenant
init = 
    { fqdn = ""
    , countryCode = ""
    , name = ""
    , tenantId = Nothing
    , partyId = 0
    , activated = False
    }

decode: JD.Decoder Tenant
decode = 
    JDP.decode Tenant
        |> JDP.optional "fqdn" JD.string ""
        |> JDP.optional "countryCode" JD.string ""
        |> JDP.optional "name" JD.string ""
        |> JDP.required "tenantId" (JD.nullable JD.int)
        |> JDP.optional "partyId" JD.int 0
        |> JDP.optional "activated" JD.bool False

encode: Tenant -> JE.Value
encode o = 
    JE.object 
        [ ( "fqdn", o.fqdn |> JE.string)
        , ( "countryCode", o.countryCode |> JE.string)
        , ( "name", o.name |> JE.string)
        , ( "tenantId", o.tenantId |> encodeMaybe JE.int)
        , ( "partyId", o.partyId |> JE.int)
        , ( "activated", o.activated |> JE.bool)
        ]



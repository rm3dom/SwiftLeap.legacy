module Types.ReferralRequest exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Types.Client as Client


{- Generated from org.swiftleap.loyalty.ReferralRequest -}

type ReferralRequestFields 
    = EmailAlt (String)
    | FirstName (String)
    | ClientId (String)
    | DisplayName (String)
    | Surname (String)
    | Client (Maybe Client.Client)
    | MobileNoAlt (String)
    | MobileNo (String)
    | Email (String)

type alias ReferralRequest =
    { emailAlt : String
    , firstName : String
    , clientId : String
    , displayName : String
    , surname : String
    , client : Maybe Client.Client
    , mobileNoAlt : String
    , mobileNo : String
    , email : String
    }

init: ReferralRequest
init = 
    { emailAlt = ""
    , firstName = ""
    , clientId = ""
    , displayName = ""
    , surname = ""
    , client = Nothing
    , mobileNoAlt = ""
    , mobileNo = ""
    , email = ""
    }

decode: JD.Decoder ReferralRequest
decode = 
    JDP.decode ReferralRequest
        |> JDP.optional "emailAlt" JD.string ""
        |> JDP.optional "firstName" JD.string ""
        |> JDP.optional "clientId" JD.string ""
        |> JDP.optional "displayName" JD.string ""
        |> JDP.optional "surname" JD.string ""
        |> JDP.required "client" (JD.nullable Client.decode)
        |> JDP.optional "mobileNoAlt" JD.string ""
        |> JDP.optional "mobileNo" JD.string ""
        |> JDP.optional "email" JD.string ""

encode: ReferralRequest -> JE.Value
encode o = 
    JE.object 
        [ ( "emailAlt", o.emailAlt |> JE.string)
        , ( "firstName", o.firstName |> JE.string)
        , ( "clientId", o.clientId |> JE.string)
        , ( "displayName", o.displayName |> JE.string)
        , ( "surname", o.surname |> JE.string)
        , ( "client", o.client |> encodeMaybe Client.encode)
        , ( "mobileNoAlt", o.mobileNoAlt |> JE.string)
        , ( "mobileNo", o.mobileNo |> JE.string)
        , ( "email", o.email |> JE.string)
        ]



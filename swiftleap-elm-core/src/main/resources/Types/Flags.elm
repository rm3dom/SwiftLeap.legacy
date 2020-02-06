module Types.Flags exposing (..)

import Json.Encode as JE
import Json.Decode as JD
import Types.Helper exposing (..)
import Json.Decode.Pipeline as JDP exposing (decode, required, optional)
import Time.DateTime as DateTime exposing (DateTime)
import Dict exposing (Dict)


{- Generated from org.swiftleap.rules.web.api.model.FlagsDto -}

type FlagsFields 
    = CompanyWebSiteUrl (String)
    | ThemeName (String)
    | ResourceUrl (String)
    | CompanyName (String)
    | WebResourceUrl (String)
    | WebSiteName (String)
    | TenantId (Int)
    | SiteName (String)
    | SessionId (String)
    | VersionHash (String)
    | GlobalUsers (Bool)

type alias Flags =
    { companyWebSiteUrl : String
    , themeName : String
    , resourceUrl : String
    , companyName : String
    , webResourceUrl : String
    , webSiteName : String
    , tenantId : Int
    , siteName : String
    , sessionId : String
    , versionHash : String
    , globalUsers : Bool
    }

init: Flags
init = 
    { companyWebSiteUrl = ""
    , themeName = ""
    , resourceUrl = ""
    , companyName = ""
    , webResourceUrl = ""
    , webSiteName = ""
    , tenantId = 0
    , siteName = ""
    , sessionId = ""
    , versionHash = ""
    , globalUsers = False
    }

decode: JD.Decoder Flags
decode = 
    JDP.decode Flags
        |> JDP.optional "companyWebSiteUrl" JD.string ""
        |> JDP.optional "themeName" JD.string ""
        |> JDP.optional "resourceUrl" JD.string ""
        |> JDP.optional "companyName" JD.string ""
        |> JDP.optional "webResourceUrl" JD.string ""
        |> JDP.optional "webSiteName" JD.string ""
        |> JDP.optional "tenantId" JD.int 0
        |> JDP.optional "siteName" JD.string ""
        |> JDP.optional "sessionId" JD.string ""
        |> JDP.optional "versionHash" JD.string ""
        |> JDP.optional "globalUsers" JD.bool False

encode: Flags -> JE.Value
encode o = 
    JE.object 
        [ ( "companyWebSiteUrl", o.companyWebSiteUrl |> JE.string)
        , ( "themeName", o.themeName |> JE.string)
        , ( "resourceUrl", o.resourceUrl |> JE.string)
        , ( "companyName", o.companyName |> JE.string)
        , ( "webResourceUrl", o.webResourceUrl |> JE.string)
        , ( "webSiteName", o.webSiteName |> JE.string)
        , ( "tenantId", o.tenantId |> JE.int)
        , ( "siteName", o.siteName |> JE.string)
        , ( "sessionId", o.sessionId |> JE.string)
        , ( "versionHash", o.versionHash |> JE.string)
        , ( "globalUsers", o.globalUsers |> JE.bool)
        ]



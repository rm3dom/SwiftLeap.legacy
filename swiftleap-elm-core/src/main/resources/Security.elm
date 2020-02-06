module Security exposing (Msg(..), Security, init, update, view)

import Components
import Components.Form as Form
import Components.Select
import CoreApi exposing (apiLogin)
import Html exposing (Html, button, div, text)
import Html.Attributes exposing (class)
import Job exposing (CoreJob, toCmd)
import Model.System as System exposing (System)
import Types.AuthRequest as AuthRequest exposing (AuthRequest)
import Types.User as User exposing (User)


type Msg
    = SetUserName String
    | SetPassword String
    | SetTenant Int
    | LoginPressed
    | LoginSuccess User


type alias Security =
    {}


init : Security
init =
    {}


update : System -> Msg -> Security -> ( System, Security, CoreJob Msg )
update system msg security =
    case system.user of
        System.LoggedIn _ ->
            ( system, security, Job.init )

        System.NotLoggedIn auth ->
            case msg of
                SetTenant tenantId ->
                    ( System.setTenantId tenantId system, security, Job.init )

                SetUserName str ->
                    ( { system | user = System.NotLoggedIn { auth | userName = str } }, security, Job.init )

                SetPassword str ->
                    ( { system | user = System.NotLoggedIn { auth | password = str } }, security, Job.init )

                LoginPressed ->
                    let
                        job =
                            Job.init
                                |> Job.addApi LoginSuccess (apiLogin auth)
                    in
                    ( system, security, job )

                LoginSuccess usr ->
                    let
                        job =
                            Job.init
                                |> Job.addAction Job.ToastHide
                                |> Job.addAction Job.LoggedIn
                    in
                    ( System.setUser (Just usr) system, security, job )


view : System -> Security -> Html Msg
view system security =
    let
        state =
            system.user

        tenant =
            system.host.tenantId
                |> String.toInt
                |> Result.withDefault 0

        tenants =
            system.tenants
                |> List.filter .activated
                |> List.sortBy .name
                |> List.map (\t -> ( t.name, Maybe.withDefault 0 t.tenantId ))

        tenantSelect =
            if system.flags.globalUsers then
                Html.text ""

            else
                Form.intSelect "Tenant" SetTenant tenant tenants
    in
    case state of
        System.LoggedIn user ->
            div [ class "login" ]
                [ text "Hello" ]

        System.NotLoggedIn auth ->
            div [ class "login" ]
                [ Components.form LoginPressed
                    [ Form.inputNoWhites "User Name" SetUserName auth.userName
                    , Form.passwordInput "Password" SetPassword auth.password
                    , tenantSelect
                    , Components.submitButton "Login"
                    ]
                ]

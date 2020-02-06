module Components.Tenants exposing (Msg(..), Tenants, init, toTenantRequest, update, validate, view, viewFilterBar, viewNav, viewTenantModal, viewTenants)

import Alfred.Validate
import AppJob exposing (AppJob)
import Components exposing (fontAwesome, form, form_, mainPanel, mainPanelContent, mainPanelMenu)
import Components.DropDown as DropDown exposing (DropDown)
import Components.Form as Form
import Components.Grid as Grid
import Components.Help as Help
import Components.Modal2 as Modal2
import CoreApi exposing (..)
import Helpers.String exposing (maybeToString)
import Html exposing (Html, a, button, div, input, nav, text)
import Html.Attributes as HA
import Html.Events as HE
import Job exposing (CoreJob)
import Model.System as System exposing (System)
import Types.NameSearchRequest as NameSearchRequest
import Types.Tenant as Tenant exposing (Tenant)
import Types.TenantRequest as TenantRequest exposing (TenantRequest)


type Msg
    = EditTenant Tenant
    | ReceiveTenant Tenant
    | SearchClicked
    | NewTenant
    | FilterStatus Int
    | FilterTenant String
    | SaveTenant
    | CancelEdit
    | SetName String
    | SetFqdn String
    | SetEmail String
    | SetUserName String
    | SetPassword String
    | SetConfirmPassword String
    | SetStatus Bool


type alias Tenants =
    { tenantStatus : Int
    , tenantFilter : String
    , tenant : Maybe TenantRequest
    , errors : List String
    , confirmPass : String
    }


init : Tenants
init =
    { tenantStatus = 1
    , tenantFilter = ""
    , tenant = Nothing
    , errors = []
    , confirmPass = ""
    }


update : System -> Msg -> Tenants -> ( System, Tenants, AppJob Msg )
update system msg model =
    case msg of
        FilterStatus int ->
            ( system, { model | tenantStatus = int }, Job.init )

        FilterTenant str ->
            ( system, { model | tenantFilter = str }, Job.init )

        SearchClicked ->
            ( system, model, Job.init )

        SaveTenant ->
            let
                ( errors, job ) =
                    case model.tenant of
                        Nothing ->
                            ( [], Job.init )

                        Just tenant ->
                            let
                                errors =
                                    validate model tenant

                                job =
                                    if List.isEmpty errors then
                                        Job.addApi ReceiveTenant (CoreApi.apiSaveTenant tenant) Job.init

                                    else
                                        Job.init
                            in
                            ( errors, job )
            in
            ( system, { model | errors = errors }, job )

        ReceiveTenant tenant ->
            ( System.addTenant tenant system, { model | tenant = Nothing }, Job.init )

        EditTenant tenant ->
            ( system, { model | tenant = Just (toTenantRequest tenant), confirmPass = "", errors = [] }, Job.init )

        NewTenant ->
            ( system, { model | tenant = Just TenantRequest.init, confirmPass = "", errors = [] }, Job.init )

        CancelEdit ->
            ( system, { model | tenant = Nothing }, Job.init )

        SetName str ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | name = str }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )

        SetFqdn str ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | fqdn = str }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )

        SetUserName str ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | userName = str }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )

        SetPassword str ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | password = str }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )

        SetEmail str ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | email = str }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )

        SetConfirmPassword str ->
            ( system, { model | confirmPass = str }, Job.init )

        SetStatus bool ->
            let
                atenant =
                    case model.tenant of
                        Nothing ->
                            Nothing

                        Just tenant ->
                            Just { tenant | activated = bool }
            in
            ( system
            , { model | tenant = atenant }
            , Job.init
            )


validate : Tenants -> TenantRequest -> List String
validate model tenant =
    Alfred.Validate.all
        [ ( Alfred.Validate.isBlank tenant.name, "Name is required" )
        , ( Alfred.Validate.isBlank tenant.userName && tenant.id == Nothing, "Admin User is required" )
        , ( Alfred.Validate.isBlank tenant.password && tenant.id == Nothing, "Password is required" )
        , ( tenant.password /= model.confirmPass, "Passwords do not match" )
        ]


toTenantRequest : Tenant -> TenantRequest
toTenantRequest tenant =
    let
        req =
            TenantRequest.init
    in
    { req
        | fqdn = tenant.fqdn
        , countryCode = tenant.countryCode
        , name = tenant.name
        , id = tenant.tenantId
        , partyId = tenant.partyId
        , activated = tenant.activated
    }


view : System -> Tenants -> Html Msg
view system model =
    let
        modalView =
            case model.tenant of
                Nothing ->
                    Html.text ""

                Just tenant ->
                    viewTenantModal system model tenant
    in
    Html.div []
        [ modalView
        , viewNav system model
        , viewTenants system model
        ]


viewTenants : System -> Tenants -> Html Msg
viewTenants system model =
    let
        yesNo bool =
            if bool then
                "Yes"

            else
                "No"

        filterStr tenant =
            String.isEmpty model.tenantFilter
                || String.contains model.tenantFilter tenant.name
                || String.contains model.tenantFilter (toString (Maybe.withDefault 0 tenant.tenantId))

        filterStatus tenant =
            case ( model.tenantStatus, tenant.activated ) of
                ( 0, _ ) ->
                    True

                ( 1, True ) ->
                    True

                ( 2, False ) ->
                    True

                ( _, _ ) ->
                    False

        filter tenant =
            filterStr tenant
                && filterStatus tenant

        tenants =
            List.filter filter system.tenants
                |> List.sortBy .name

        actions tenant =
            if tenant.tenantId == Just 0 then
                Html.text ""

            else
                DropDown.init
                    |> DropDown.add "Edit" "fa-pencil-square-o" (EditTenant tenant)
                    --|> DropDown.add "Disable" "fa-minus-square-o" NoOp
                    --|> DropDown.add "Enable" "fa-plus-square-o" NoOp
                    |> DropDown.renderAnchor

        grid =
            Grid.empty
                |> Grid.addCol "Id" (.tenantId >> maybeToString)
                |> Grid.addCol "Name" .name
                |> Grid.addCol "Country" .countryCode
                |> Grid.addCol "FQDN" .fqdn
                |> Grid.addCol "Activated" (yesNo << .activated)
                |> Grid.addColExt (Html.text "Actions") Grid.Left False actions
                |> Grid.render tenants
    in
    div [] [ grid ]


viewNav : System -> Tenants -> Html Msg
viewNav system model =
    nav
        [ HA.class "navbar navbar-default" ]
        [ button
            [ HE.onClick NewTenant
            , HA.type_ "button"
            , HA.class "margin-l10 btn navbar-left btn-primary navbar-btn"
            ]
            [ text "New Tenant" ]
        , viewFilterBar model
        , Help.helpLinkStyle [ ( "float", "right" ), ( "padding", "15px" ) ] "Tenants" system
        ]


viewFilterBar : Tenants -> Html Msg
viewFilterBar model =
    let
        statusOptions =
            [ ( "All Tenants", 0 )
            , ( "Active Tenants", 1 )
            , ( "Disabled Tenants", 2 )
            ]
    in
    Components.form_ SearchClicked
        [ HA.class "navbar-form navbar-left margin-l10", HA.attribute "role" "search" ]
        [ Html.div [ HA.class "form-group margin-r5" ]
            [ Components.intSelect FilterStatus statusOptions model.tenantStatus
            ]
        , Html.div [ HA.class "form-group margin-r5" ]
            [ Html.input [ HA.value model.tenantFilter, HE.onInput FilterTenant, HA.class "form-control" ] []
            ]
        ]


viewTenantModal : System -> Tenants -> TenantRequest -> Html Msg
viewTenantModal system model tenant =
    let
        passwordsMatch =
            if model.confirmPass /= tenant.password then
                Html.b [] [ Html.text "Passwords do not match" ]

            else
                Html.text ""

        tenantView =
            [ Components.warn model.errors
            , Form.inputNoWhites "Name" SetName tenant.name
            , Form.inputNoWhites "FQDN" SetFqdn tenant.fqdn
            , Form.toggleInput "Active" SetStatus tenant.activated
            ]

        formView =
            case tenant.id of
                Nothing ->
                    tenantView
                        ++ [ Form.inputNoWhites "Admin User" SetUserName tenant.userName
                           , Form.inputNoWhites "EMail" SetEmail tenant.email
                           , Form.passwordInput "Password" SetPassword tenant.password
                           , Form.passwordInput "Confirm Password" SetConfirmPassword model.confirmPass
                           , passwordsMatch
                           ]

                Just _ ->
                    tenantView

        save =
            Form.submitButton "Save"
    in
    Form.form SaveTenant
        [ Modal2.viewLarge "Tenant" Modal2.shown CancelEdit (Html.div [] formView) (Just save)
        ]

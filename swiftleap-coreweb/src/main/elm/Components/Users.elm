module Components.Users exposing (Msg, Users, init, update, view)

import Alfred.Validate
import AppJob exposing (AppJob)
import Bootstrap.Grid as Layout
import Bootstrap.Grid.Col
import Components
import Components.DropDown as DropDown exposing (DropDown)
import Components.Form as Form
import Components.Grid as Grid
import Components.Help as Help exposing (helpLinkStyle)
import Components.Input as Input exposing (Input)
import Components.Modal2 as Modal2
import Components.Select as Select
import CoreApi
import Html exposing (Html)
import Html.Attributes as HA
import Html.Events as HE
import Job exposing (CoreJob)
import Model.System as System exposing (System)
import Types.User as User exposing (User)
import Types.UserRequest as UserRequest exposing (UserRequest)


type alias Users =
    { userStatus : Int
    , userFilter : String
    , user : Maybe UserRequest
    , confirmPass : String
    , errors : List String
    }


type Msg
    = NewUserClicked
    | FilterStatus Int
    | FilterUser String
    | SearchClicked
    | Edit User
    | Enable User
    | Disable User
    | CloseUserModal
    | ToggleRole String
    | SetUserName String
    | SetUserFirstName String
    | SetUserSurname String
    | SetUserEmail String
    | SetUserStatus Bool
    | CreateNewUser
    | SetUserPassword String
    | SetUserConfirmPassword String
    | ReceiveUser User


userModal =
    "userModal"


init : Users
init =
    { userStatus = 1
    , userFilter = ""
    , user = Nothing
    , confirmPass = ""
    , errors = []
    }


toUserRequest : User -> UserRequest
toUserRequest user =
    let
        req =
            UserRequest.init
    in
    { req
        | id = user.id
        , firstName = user.firstName
        , surname = user.surname
        , roles = user.roles
        , tenantId = user.tenantId
        , description = user.name
        , userName = user.userName
        , activated = user.activated
        , email = user.email
        , password = "************"
    }


update : System -> Msg -> Users -> ( System, Users, AppJob Msg )
update system msg model =
    case msg of
        ToggleRole role ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            let
                                roles =
                                    if List.any ((==) role) user.roles then
                                        List.filter ((/=) role) user.roles

                                    else
                                        List.append [ role ] user.roles
                            in
                            Just { user | roles = roles }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserStatus bool ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | activated = bool }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserName str ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | userName = str }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserFirstName str ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | firstName = str }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserSurname str ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | surname = str }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserEmail str ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | email = str }
            in
            ( system, { model | user = auser }, Job.init )

        SetUserPassword str ->
            let
                auser =
                    case model.user of
                        Nothing ->
                            Nothing

                        Just user ->
                            Just { user | password = str }
            in
            ( system
            , { model | user = auser }
            , Job.init
            )

        Edit user ->
            let
                userRequest =
                    toUserRequest user
            in
            ( system
            , { model
                | user = Just userRequest
                , confirmPass = userRequest.password
                , errors = []
              }
            , Job.init
            )

        Enable user ->
            let
                userRequest =
                    toUserRequest user

                disabledUser =
                    { userRequest | activated = True }
            in
            ( system
            , model
            , Job.addApi ReceiveUser (CoreApi.apiUser disabledUser) Job.init
            )

        Disable user ->
            let
                userRequest =
                    toUserRequest user

                disabledUser =
                    { userRequest | activated = False }
            in
            ( system
            , model
            , Job.addApi ReceiveUser (CoreApi.apiUser disabledUser) Job.init
            )

        SearchClicked ->
            ( system, model, Job.init )

        FilterStatus int ->
            ( system, { model | userStatus = int }, Job.init )

        FilterUser userFilter ->
            ( system
            , { model | userFilter = userFilter }
            , Job.init
            )

        NewUserClicked ->
            ( system
            , { model | user = Just UserRequest.init, confirmPass = "", errors = [] }
            , Job.init
            )

        CloseUserModal ->
            ( system
            , { model | user = Nothing }
            , Job.init
            )

        CreateNewUser ->
            let
                ( errors, job ) =
                    case model.user of
                        Nothing ->
                            ( [], Job.init )

                        Just user ->
                            let
                                errors =
                                    validate model user

                                job =
                                    if List.isEmpty errors then
                                        Job.addApi ReceiveUser (CoreApi.apiUser user) Job.init

                                    else
                                        Job.init
                            in
                            ( errors, job )
            in
            ( system, { model | errors = errors }, job )

        SetUserConfirmPassword str ->
            ( system, { model | confirmPass = str }, Job.init )

        ReceiveUser user ->
            ( System.addUser user system
            , { model | user = Nothing }
            , Job.init
            )


validate : Users -> UserRequest -> List String
validate model user =
    Alfred.Validate.all
        [ ( Alfred.Validate.isBlank user.userName, "User Name is required" )
        , ( Alfred.Validate.isBlank user.password && user.password /= "************", "Password is required" )
        , ( user.password /= model.confirmPass, "Passwords do not match" )
        ]


view : System -> Users -> Html Msg
view system model =
    let
        modalView =
            case model.user of
                Nothing ->
                    Html.text ""

                Just user ->
                    viewUserModal system model user
    in
    Html.div []
        [ modalView
        , viewNav system model
        , viewUsers system model
        ]


viewNav : System -> Users -> Html Msg
viewNav system model =
    Html.nav
        [ HA.class "navbar navbar-default" ]
        [ Html.button
            [ HA.type_ "button", HE.onClick NewUserClicked, HA.class "margin-l10 btn navbar-left btn-primary navbar-btn" ]
            [ Html.text "New User" ]
        , viewFilterBar model
        , Help.helpLinkStyle [ ( "float", "right" ), ( "padding", "15px" ) ] "Users" system
        ]


viewFilterBar : Users -> Html Msg
viewFilterBar model =
    let
        statusOptions =
            [ ( "All Users", 0 )
            , ( "Active Users", 1 )
            , ( "Disabled Users", 2 )
            ]
    in
    Components.form_ SearchClicked
        [ HA.class "navbar-form navbar-left margin-l10", HA.attribute "role" "search" ]
        [ Html.div [ HA.class "form-group margin-r5" ]
            [ Components.intSelect FilterStatus statusOptions model.userStatus
            ]
        , Html.div [ HA.class "form-group margin-r5" ]
            [ Html.input [ HA.value model.userFilter, HE.onInput FilterUser, HA.class "form-control" ] []
            ]
        ]


viewUsers : System -> Users -> Html Msg
viewUsers system model =
    let
        yesNo bool =
            if bool then
                "Yes"

            else
                "No"

        actions user =
            if user.userName == "system" then
                Html.text ""

            else if user.activated then
                DropDown.init
                    |> DropDown.add "Edit" "fa-pencil-square-o" (Edit user)
                    |> DropDown.add "Disable" "fa-minus-square-o" (Disable user)
                    |> DropDown.renderAnchor

            else
                DropDown.init
                    |> DropDown.add "Edit" "fa-pencil-square-o" (Edit user)
                    |> DropDown.add "Enable" "fa-plus-square-o" (Enable user)
                    |> DropDown.renderAnchor

        filterStr user =
            String.isEmpty model.userFilter
                || String.contains model.userFilter user.firstName
                || String.contains model.userFilter user.surname
                || String.contains model.userFilter user.userName

        filterStatus user =
            case ( model.userStatus, user.activated ) of
                ( 0, _ ) ->
                    True

                ( 1, True ) ->
                    True

                ( 2, False ) ->
                    True

                ( _, _ ) ->
                    False

        filter user =
            filterStr user
                && filterStatus user

        users =
            List.filter filter system.users
                |> List.sortBy .userName

        grid =
            Grid.empty
                |> Grid.addCol "User" .userName
                |> Grid.addCol "Name" .firstName
                |> Grid.addCol "Surname" .surname
                |> Grid.addCol "Email" .email
                |> Grid.addCol "Activated" (yesNo << .activated)
                |> Grid.addColExt (Html.text "Actions") Grid.Left False actions
                |> Grid.render users
    in
    Html.div []
        [ grid
        ]


viewUserModal : System -> Users -> UserRequest -> Html Msg
viewUserModal system model user =
    let
        passwordsMatch =
            if model.confirmPass /= user.password then
                Html.b [] [ Html.text "Passwords do not match" ]

            else
                Html.text ""

        roles =
            system.roles
                |> List.map (\pair -> ( pair.value1, pair.value2 ))

        userView =
            Html.div []
                [ Components.warn model.errors
                , Form.inputNoWhites_ "User" SetUserName user.userName [ HA.autocomplete False ]
                , Form.inputNoWhites_ "Name" SetUserFirstName user.firstName [ HA.autocomplete False ]
                , Form.inputNoWhites_ "Surname" SetUserSurname user.surname [ HA.autocomplete False ]
                , Form.inputNoWhites_ "EMail" SetUserEmail user.email [ HA.autocomplete False ]
                , Form.toggleInput "Active" SetUserStatus user.activated
                , Form.passwordInput "Password" SetUserPassword user.password
                , Form.passwordInput "Confirm Password" SetUserConfirmPassword model.confirmPass
                , passwordsMatch
                , Select.multiSelect ToggleRole user.roles roles Select.init |> Form.group "Roles"
                ]

        save =
            Form.submitButton "Save"
    in
    Form.form_ CreateNewUser
        [ HA.autocomplete False ]
        [ Modal2.viewLarge "User" Modal2.shown CloseUserModal userView (Just save)
        ]

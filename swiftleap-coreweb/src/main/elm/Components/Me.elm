module Components.Me exposing (Me, Msg, init, initLoggedIn, update, view)

import AppJob exposing (AppJob)
import Components
import Components.Form as Form
import CoreApi
import Html exposing (Html, button, div, input, li, nav, option, select, text, ul)
import Html.Attributes exposing (attribute, class, readonly, style, type_, value)
import Job exposing (CoreJob)
import Model.System as System exposing (System)
import Security as Security exposing (Security)
import Types.User as User exposing (User)
import Types.UserRequest as UserRequest exposing (UserRequest)


type alias Me =
    { confirmPass : String
    , showPassword : Bool
    , user : UserRequest
    }


type Msg
    = SetUserFirstName String
    | SetUserSurname String
    | SetUserEmail String
    | SetUserPassword String
    | SetUserConfirmPassword String
    | ChangePassword
    | ReceiveUser User
    | Save
    | Reset


init : Me
init =
    { confirmPass = ""
    , showPassword = False
    , user = UserRequest.init
    }


initLoggedIn : System -> Me
initLoggedIn system =
    { confirmPass = ""
    , showPassword = False
    , user = toUserRequest (System.getUser system)
    }


update : System -> Msg -> Me -> ( System, Me, AppJob Msg )
update system msg model =
    case msg of
        SetUserFirstName str ->
            let
                auser =
                    model.user
            in
            ( system
            , { model | user = { auser | firstName = str } }
            , Job.init
            )

        SetUserSurname str ->
            let
                auser =
                    model.user
            in
            ( system
            , { model | user = { auser | surname = str } }
            , Job.init
            )

        SetUserEmail str ->
            let
                auser =
                    model.user
            in
            ( system
            , { model | user = { auser | email = str } }
            , Job.init
            )

        SetUserPassword str ->
            let
                auser =
                    model.user
            in
            ( system
            , { model | user = { auser | password = str } }
            , Job.init
            )

        SetUserConfirmPassword str ->
            ( system
            , { model | confirmPass = str }
            , Job.init
            )

        ChangePassword ->
            let
                auser =
                    model.user
            in
            ( system
            , { model | user = { auser | password = "" }, confirmPass = "", showPassword = True }
            , Job.init
            )

        Save ->
            let
                job =
                    if model.confirmPass /= model.user.password then
                        Job.init

                    else
                        Job.addApi ReceiveUser (CoreApi.apiUser model.user) Job.init
            in
            ( system
            , model
            , job
            )

        Reset ->
            ( system
            , { model | user = toUserRequest (System.getUser system), showPassword = False, confirmPass = "" }
            , Job.init
            )

        ReceiveUser user ->
            let
                curUser =
                    case System.getUser system of
                        Nothing ->
                            Nothing

                        Just auser ->
                            Just
                                { auser
                                    | firstName = user.firstName
                                    , email = user.email
                                    , surname = user.surname
                                }
            in
            ( system
                |> System.setUser curUser
                |> System.addUser user
            , { model | user = toUserRequest curUser, showPassword = False }
            , Job.addAction (Job.ToastInfo "Your information was updated") Job.init
            )


view : System -> Me -> Html Msg
view system model =
    let
        passwordsMatch =
            if model.confirmPass /= model.user.password then
                Html.b [] [ Html.text "Passwords do not match" ]

            else
                Html.text ""

        user =
            model.user

        sysUser =
            Maybe.withDefault User.init (System.getUser system)

        passwordView =
            if model.showPassword then
                Html.div []
                    [ Form.passwordInput "Password" SetUserPassword user.password
                    , Form.passwordInput "Confirm Password" SetUserConfirmPassword model.confirmPass
                    , passwordsMatch
                    ]

            else
                Components.hblock [ Form.button ChangePassword "Change Password" ]
    in
    Components.mainPanel []
        [ Components.mainPanelContent []
            [ div [ class "row" ]
                [ div [ class "col col-sm-2" ]
                    [ Components.userImage sysUser.imageMime sysUser.image

                    --, Components.hblock [ Components.button Core.NoOp "Change Image" ]
                    ]
                , div [ class "col col-sm-10" ]
                    [ Components.formReadonlyInput "User Name" user.userName
                    , Form.input "EMail" SetUserEmail user.email
                    , Form.input "First Name" SetUserFirstName user.firstName
                    , Form.input "Surname" SetUserSurname user.surname
                    , passwordView
                    , Components.hblock
                        [ Form.primaryButton Save "Save"
                        , Form.button Reset "Reset"
                        ]
                    ]
                ]
            ]
        ]


toUserRequest : Maybe User -> UserRequest
toUserRequest auser =
    let
        user =
            Maybe.withDefault User.init auser
    in
    { id = user.id
    , firstName = user.firstName
    , password = ""
    , surname = user.surname
    , roles = user.roles
    , tenantId = user.tenantId
    , description = user.name
    , partyId = Nothing
    , userName = user.userName
    , activated = user.activated
    , email = user.email
    }

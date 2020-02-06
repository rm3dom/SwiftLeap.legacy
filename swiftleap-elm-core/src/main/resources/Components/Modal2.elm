module Components.Modal2 exposing (..)

import Bootstrap.Grid as Grid
import Bootstrap.Grid.Col as Col
import Bootstrap.Modal as Modal exposing (Config)
import Html exposing (Html)
import Html.Attributes as HA


type alias Visibility =
    Modal.Visibility


shown : Visibility
shown =
    Modal.shown


{-| The modal should be hidden
-}
hidden : Visibility
hidden =
    Modal.hiddenAnimated


viewSmall : String -> Modal.Visibility -> msg -> Html msg -> Maybe (Html msg) -> Html msg
viewSmall =
    view_ Modal.small


viewLarge : String -> Modal.Visibility -> msg -> Html msg -> Maybe (Html msg) -> Html msg
viewLarge =
    view_ Modal.large


viewExtraLarge : String -> Modal.Visibility -> msg -> Html msg -> Maybe (Html msg) -> Html msg
viewExtraLarge title visibility closeMsg content footer =
    Html.div [ HA.class "modal-xxxl" ] [ view_ Modal.large title visibility closeMsg content footer ]


view_ : (Config msg -> Config msg) -> String -> Modal.Visibility -> msg -> Html msg -> Maybe (Html msg) -> Html msg
view_ config title visibility closeMsg content footer =
    let
        footerView =
            Maybe.withDefault (Html.text "") footer
    in
    Modal.config closeMsg
        |> config
        |> Modal.h5 [] [ Html.text title ]
        |> Modal.body [] [ content ]
        |> Modal.footer [] [ footerView ]
        |> Modal.view visibility

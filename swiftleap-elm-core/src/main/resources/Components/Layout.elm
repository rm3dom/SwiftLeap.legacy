module Components.Layout exposing (..)

import Html exposing (Html)
import Html.Attributes as HA


row : List (Html msg) -> Html msg
row =
    Html.div [ HA.class "row" ]


row_ : List (Html.Attribute msg) -> List (Html msg) -> Html msg
row_ attribs =
    Html.div (List.append attribs [ HA.class "row" ])


col : String -> List (Html msg) -> Html msg
col class =
    Html.div [ HA.class ("col " ++ class) ]


colSm1 : List (Html msg) -> Html msg
colSm1 =
    col "col-sm-1"


colSm2 : List (Html msg) -> Html msg
colSm2 =
    col "col-sm-2"


colSm3 : List (Html msg) -> Html msg
colSm3 =
    col "col-sm-3"

colSm4 : List (Html msg) -> Html msg
colSm4 =
    col "col-sm-4"

colSm6 : List (Html msg) -> Html msg
colSm6 =
    col "col-sm-6"


colSm8 : List (Html msg) -> Html msg
colSm8 =
    col "col-sm-8"

colSm9 : List (Html msg) -> Html msg
colSm9 =
    col "col-sm-9"


colSm11 : List (Html msg) -> Html msg
colSm11 =
    col "col-sm-11"


colAuto : List (Html msg) -> Html msg
colAuto =
    col ""


divPadH15 : List (Html msg) -> Html msg
divPadH15 body =
    Html.div [ HA.class "hblock" ] body


divPad15 : List (Html msg) -> Html msg
divPad15 body =
    Html.div [ HA.class "block" ] body

module Components.Event exposing (..)

import Html exposing (Attribute, Html)
import Html.Events as HE exposing (onClick, onSubmit)
import Json.Decode as JD


onChange : (String -> msg) -> Html.Attribute msg
onChange tagger =
    HE.on "change" (JD.map tagger HE.targetValue)


noBubble : HE.Options
noBubble =
    { stopPropagation = True
    , preventDefault = True
    }


onChangeNoBubble : (String -> msg) -> Html.Attribute msg
onChangeNoBubble tagger =
    HE.onWithOptions "change" noBubble (JD.map tagger HE.targetValue)


onClickNoBubble : msg -> Html.Attribute msg
onClickNoBubble tagger =
    HE.onWithOptions "click" noBubble (JD.succeed tagger)

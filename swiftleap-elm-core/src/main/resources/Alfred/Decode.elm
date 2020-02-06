module Alfred.Decode exposing (optional, required)

import Alfred.Maybe
import Json.Decode as JD


optional : String -> JD.Decoder a -> a -> (a -> field) -> (field -> model -> model) -> model -> JD.Decoder model
optional jsonField decoder default toField update model =
    JD.field jsonField decoder
        |> JD.maybe
        |> JD.map (Alfred.Maybe.unwrap (toField default) toField)
        |> JD.map (\field -> update field model)


required : String -> JD.Decoder a -> (a -> field) -> (field -> model -> model) -> model -> JD.Decoder model
required jsonField decoder toField update model =
    JD.field jsonField decoder
        |> JD.map toField
        |> JD.map (\field -> update field model)

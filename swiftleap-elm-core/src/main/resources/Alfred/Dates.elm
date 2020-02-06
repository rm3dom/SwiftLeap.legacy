module Alfred.Dates exposing (..)

import Date exposing (Date, Month(..))
import Regex
import Time.DateTime as DateTime exposing (DateTime, zero)


-----------------------
-- Alfred does dates --
-----------------------


initDateTime : DateTime
initDateTime =
    DateTime.fromTuple ( 1900, 1, 1, 0, 0, 0, 0 )


endOfTime : DateTime
endOfTime =
    DateTime.fromTuple ( 2999, 12, 31, 0, 0, 0, 0 )


dateTimeToDate : DateTime -> Date
dateTimeToDate =
    DateTime.toTimestamp >> Date.fromTime


dateToDateTime : Date -> DateTime
dateToDateTime =
    Date.toTime >> DateTime.fromTimestamp


{-| 5/6/2017
-}
toDate : DateTime -> String
toDate dateTime =
    let
        valid =
            after dateTime initDateTime
                && before dateTime endOfTime
    in
    if valid then
        (dateTime |> DateTime.day |> toString |> padDate)
            ++ "/"
            ++ (dateTime |> DateTime.month |> toString |> padDate)
            ++ "/"
            ++ (dateTime |> DateTime.year |> toString |> padYear)
    else
        ""


{-| 5/6/2017
-}
toTime : DateTime -> String
toTime dateTime =
    let
        valid =
            after dateTime initDateTime
                && before dateTime endOfTime
    in
    if valid then
        (dateTime |> DateTime.day |> toString |> padDate)
            ++ "/"
            ++ (dateTime |> DateTime.month |> toString |> padDate)
            ++ "/"
            ++ (dateTime |> DateTime.year |> toString |> padYear)
            ++ " "
            ++ (dateTime |> DateTime.hour |> toString |> padDate)
            ++ ":"
            ++ (dateTime |> DateTime.minute |> toString |> padDate)
            ++ ":"
            ++ (dateTime |> DateTime.second |> toString |> padDate)
    else
        ""


toTimePortion : DateTime -> String
toTimePortion dateTime =
    let
        valid =
            after dateTime initDateTime
                && before dateTime endOfTime
    in
    if valid then
        (dateTime |> DateTime.hour |> toString |> padDate)
            ++ ":"
            ++ (dateTime |> DateTime.minute |> toString |> padDate)
            ++ ":"
            ++ (dateTime |> DateTime.second |> toString |> padDate)
    else
        ""


toDateWithDefault : String -> Maybe DateTime -> String
toDateWithDefault default maybeDate =
    maybeDate
        |> Maybe.map toDate
        |> Maybe.withDefault default


clamp : DateTime -> DateTime -> DateTime -> DateTime
clamp low high target =
    Basics.clamp (DateTime.toTimestamp low) (DateTime.toTimestamp high) (DateTime.toTimestamp target)
        |> DateTime.fromTimestamp


isMaybeValid : Maybe DateTime -> Bool
isMaybeValid date =
    case date of
        Nothing ->
            False

        Just someDate ->
            not (isBeginningOfTime someDate || isEndOfTime someDate)


isBeginningOfTime : DateTime -> Bool
isBeginningOfTime dateTime =
    DateTime.compare dateTime initDateTime /= GT


isEndOfTime : DateTime -> Bool
isEndOfTime dateTime =
    DateTime.compare dateTime endOfTime /= LT


dateEquals : DateTime -> DateTime -> Bool
dateEquals a b =
    DateTime.date a == DateTime.date b


dateTimeEquals : DateTime -> DateTime -> Bool
dateTimeEquals a b =
    DateTime.toTuple a == DateTime.toTuple b


before : DateTime -> DateTime -> Bool
before a b =
    DateTime.compare a b == LT


after : DateTime -> DateTime -> Bool
after a b =
    DateTime.compare a b == GT


beforeOrEqual : DateTime -> DateTime -> Bool
beforeOrEqual a b =
    before a b || dateTimeEquals a b


afterOrEqual : DateTime -> DateTime -> Bool
afterOrEqual a b =
    after a b || dateTimeEquals a b


toDateTime : String -> DateTime
toDateTime str =
    toMaybeDateTime str
        |> Maybe.withDefault initDateTime


toMaybeDateTime : String -> Maybe DateTime
toMaybeDateTime str =
    parseDateToISO8601 str
        |> DateTime.fromISO8601
        |> Result.map (\date -> Just date)
        |> Result.withDefault Nothing


lastFinancialYear : DateTime -> Period {}
lastFinancialYear current =
    let
        july =
            7

        lastYearsFinancialStart =
            DateTime.dateTime
                { zero
                    | year = DateTime.year current - 1
                    , month = july
                    , day = 1
                }

        lastYearsFinancialEnd =
            lastYearsFinancialStart
                |> DateTime.addYears 1
                |> DateTime.addDays -1
    in
    if DateTime.month current >= july then
        { startDate = lastYearsFinancialStart
        , endDate = lastYearsFinancialEnd
        }
    else
        { startDate = lastYearsFinancialStart |> DateTime.addYears -1
        , endDate = lastYearsFinancialEnd |> DateTime.addYears -1
        }


parseDateToISO8601 : String -> String
parseDateToISO8601 stringDate =
    let
        dmy =
            stringDate
                |> Regex.split Regex.All (Regex.regex "[-/. ]")
                |> List.map (String.toInt >> Result.toMaybe)
                |> List.filter ((/=) Nothing)

        toIsoStrings dmyStringList =
            dmyStringList
                |> List.map padDate
                |> String.join "-"
                |> flip (++) "T00:00:00Z"

        toIso dmyList =
            dmyList
                |> List.map toString
                |> toIsoStrings
    in
    if List.length dmy == 1 then
        toIsoStrings (Debug.log "date" [ String.slice 4 8 stringDate, String.slice 2 4 stringDate, String.slice 0 2 stringDate ])
    else
        case dmy of
            (Just d) :: (Just m) :: (Just y) :: _ ->
                if d < 32 && m < 13 && y > 1000 then
                    toIso [ y, m, d ]
                else if y < 32 && m < 13 && d > 1000 then
                    toIso [ d, m, y ]
                else
                    ""

            (Just _) :: [] ->
                toIsoStrings [ String.slice 4 10 stringDate, String.slice 2 4 stringDate, String.slice 0 2 stringDate ]

            _ ->
                ""


{-| [date] 12:40
-}
toMinuteResolution : DateTime -> String
toMinuteResolution dateTime =
    toDate dateTime ++ " " ++ toHoursAndMinutes dateTime


{-| 12:40
-}
toHoursAndMinutes : DateTime -> String
toHoursAndMinutes dateTime =
    toString (DateTime.hour dateTime)
        ++ ":"
        ++ (toString (DateTime.minute dateTime) |> padDate)


padDate : String -> String
padDate =
    String.padLeft 2 '0'


padYear : String -> String
padYear =
    String.padLeft 4 '0'


toExpiryDate : DateTime -> String
toExpiryDate dateTime =
    String.slice 3 10 (toDate dateTime)


{-| A period is any record with a startDate and endDate.
-}
type alias Period a =
    { a | startDate : DateTime, endDate : DateTime }


{-| Old date form, deprecated.
-}
type alias PeriodStr a =
    { a | startDate : String, endDate : String }


{-| Filter on a period.
Example: [] |> filterPeriod effDate
-}
filterPeriod : DateTime -> List (Period a) -> List (Period a)
filterPeriod eff list =
    let
        filter period =
            let
                endCmp =
                    DateTime.compare period.endDate eff

                startCmp =
                    DateTime.compare period.startDate eff
            in
            startCmp /= GT && endCmp == GT
    in
    list |> List.filter filter


whatMonthIsThis : Month -> Int
whatMonthIsThis month =
    case month of
        Jan ->
            1

        Feb ->
            2

        Mar ->
            3

        Apr ->
            4

        May ->
            5

        Jun ->
            6

        Jul ->
            7

        Aug ->
            8

        Sep ->
            9

        Oct ->
            10

        Nov ->
            11

        Dec ->
            12

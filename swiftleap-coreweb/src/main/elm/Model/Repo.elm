module Model.Repo exposing
    ( CallableName(..)
    , Repo
    , ShowInfo(..)
    , View(..)
    , addGroup
    , addRule
    , addTerm
    , deleteRule
    , deleteTerm
    , getCallableName
    , getEditRule
    , getEditTerm
    , getGroup
    , getGroupName
    , getRule
    , getRuleName
    , getRules
    , getSchema
    , getSchemaFields
    , getSelectedGroup
    , getSelectedGroupName
    , getTerm
    , getTermName
    , getTerms
    , getVersions
    , hasChildren
    , init
    , pluginGroup
    , removeGroup
    , ruleNameExists
    , rulesGroup
    , setGroups
    , setEditRule
    , setEditTerm
    , updateEditTerm
    , updateEditRule
    , setRules
    , setSchema
    , setSelectedGroup
    , setShowInfo
    , setTerms
    , setVersions
    , systemGroup
    , termNameExists
    , termsGroup
    , toggleExpandedGroup
    , fieldTypes
    , getPrettyFieldType
    )

import Dict exposing (Dict)
import Types.Rule exposing (Rule)
import Types.RuleGroup exposing (RuleGroup)
import Types.RuleInfo exposing (RuleInfo)
import Types.RuleVersion exposing (RuleVersion)
import Types.Schema exposing (Schema)
import Types.Term exposing (Term)
import Types.TermInfo exposing (TermInfo)


type ShowInfo
    = ShowInfoNothing
    | ShowInfoTerm Term
    | ShowInfoRule Rule


type View
    = ViewGroup Int
    | ViewEditTerm Int Term
    | ViewEditRule Int Rule


type CallableName
    = CallableRuleName String
    | CallableTermName String


type alias Repo =
    { groups : List RuleGroup
    , expandedGroups : Dict Int Int
    , terms : List TermInfo
    , rules : List RuleInfo
    , versions : List RuleVersion
    , schema : Schema
    , showInfo : ShowInfo
    , view : View
    }


init : Repo
init =
    let
        repo =
            { groups = []
            , expandedGroups = Dict.empty
            , terms = []
            , rules = []
            , versions = []
            , schema = Types.Schema.init
            , showInfo = ShowInfoNothing
            , view = ViewGroup rulesGroup
            }
    in
    repo
        |> toggleExpandedGroup rulesGroup


systemGroup : number
systemGroup =
    10000000


pluginGroup : number
pluginGroup =
    10000001


termsGroup : number
termsGroup =
    10000002


rulesGroup : number
rulesGroup =
    10000003


setShowInfo : ShowInfo -> Repo -> Repo
setShowInfo showInfo model =
    { model | showInfo = showInfo }


getEditRule : Repo -> Maybe Rule
getEditRule model =
    case model.view of
        ViewEditRule _ rule ->
            Just rule

        _ ->
            Nothing


getEditTerm : Repo -> Maybe Term
getEditTerm model =
    case model.view of
        ViewEditTerm _ term ->
            Just term

        _ ->
            Nothing

updateEditTerm : (Term -> Term) -> Repo -> Repo
updateEditTerm func model =
    case model.view of
        ViewEditTerm group term ->
            {model | view = ViewEditTerm group (func term) }

        _ ->
            model

updateEditRule : (Rule -> Rule) -> Repo -> Repo
updateEditRule func model =
    case model.view of
        ViewEditRule group rule ->
            {model | view = ViewEditRule group (func rule) }

        _ ->
            model

-- Callable


getCallableName : String -> Repo -> CallableName
getCallableName id model =
    let
        termName =
            getTermName id model
    in
    if String.isEmpty termName then
        CallableRuleName (getRuleName id model)

    else
        CallableTermName termName



--Groups


toggleExpandedGroup : Int -> Repo -> Repo
toggleExpandedGroup id model =
    let
        contains =
            Dict.get id model.expandedGroups /= Nothing

        expandedGroups =
            if contains then
                Dict.remove id model.expandedGroups

            else
                Dict.insert id id model.expandedGroups
    in
    { model | expandedGroups = expandedGroups }


setSelectedGroup : Int -> Repo -> Repo
setSelectedGroup id model =
    { model | view = ViewGroup id }


setEditTerm : Term -> Repo -> Repo
setEditTerm term model =
    { model | view = ViewEditTerm term.groupId term }


setEditRule : Rule -> Repo -> Repo
setEditRule rule model =
    { model | view = ViewEditRule rule.groupId rule }


getSelectedGroup : Repo -> Int
getSelectedGroup model =
    case model.view of
        ViewGroup id ->
            id

        ViewEditRule id _ ->
            id

        ViewEditTerm id _ ->
            id


getGroup : Int -> Repo -> Maybe RuleGroup
getGroup groupId model =
    model.groups
        |> List.filter (\g -> g.id == groupId)
        |> List.head


getGroupName : Int -> Repo -> String
getGroupName groupId model =
    model.groups
        |> List.filter (\g -> g.id == groupId)
        |> List.head
        |> Maybe.map .name
        |> Maybe.withDefault ""


getSelectedGroupName : Repo -> String
getSelectedGroupName model =
    getGroupName (getSelectedGroup model) model


setGroups : List RuleGroup -> Repo -> Repo
setGroups groups model =
    { model | groups = groups }


addGroup : RuleGroup -> Repo -> Repo
addGroup group model =
    { model | groups = group :: List.filter (\g -> g.id /= group.id) model.groups }


removeGroup : Int -> Repo -> Repo
removeGroup groupId model =
    { model | groups = List.filter (\g -> g.id /= groupId) model.groups }


hasChildren : RuleGroup -> Repo -> Bool
hasChildren group model =
    List.any (\g -> g.parentId == group.id) model.groups



--Terms


getTermName : String -> Repo -> String
getTermName id model =
    model.terms
        |> List.filter (\t -> t.id == id)
        |> List.map .name
        |> List.head
        |> Maybe.withDefault ""


termNameExists : Term -> Repo -> Bool
termNameExists term model =
    List.any (\t -> t.id /= term.id && String.toLower t.name == String.toLower term.name) model.terms


getTerms : Int -> Repo -> List TermInfo
getTerms groupId model =
    model.terms
        |> List.filter (\t -> t.groupId == groupId)


getTerm : String -> Repo -> Maybe TermInfo
getTerm id repo =
    repo.terms
        |> List.filter (\t -> t.id == id)
        |> List.head


setTerms : List TermInfo -> Repo -> Repo
setTerms terms model =
    { model | terms = terms }


addTerm : Term -> Repo -> Repo
addTerm term model =
    let
        termInfo =
            { groupId = term.groupId
            , name = term.name
            , description = term.description
            , language = term.language
            , id = term.id
            , creationTime = term.creationTime
            , createdBy = term.createdBy
            , lastUpdateTime = term.lastUpdateTime
            , lastUpdatedBy = term.lastUpdatedBy
            , url = term.url
            }

        terms =
            termInfo :: List.filter (\t -> t.id /= term.id) model.terms
    in
    { model | terms = terms }


deleteTerm : String -> Repo -> Repo
deleteTerm termId model =
    { model
    | terms = List.filter (\r -> r.id /= termId) model.terms
    , view = ViewGroup (getSelectedGroup model)
    }



--Rules


getRule : String -> Repo -> Maybe RuleInfo
getRule id repo =
    repo.rules
        |> List.filter (\t -> t.id == id)
        |> List.head


getRuleName : String -> Repo -> String
getRuleName id model =
    model.rules
        |> List.filter (\t -> t.id == id)
        |> List.map .name
        |> List.head
        |> Maybe.withDefault ""


ruleNameExists : Rule -> Repo -> Bool
ruleNameExists rule model =
    List.any (\t -> t.id /= rule.id && String.toLower t.name == String.toLower rule.name) model.terms


getRules : Int -> Repo -> List RuleInfo
getRules groupId model =
    model.rules
        |> List.filter (\t -> t.groupId == groupId)


setRules : List RuleInfo -> Repo -> Repo
setRules list model =
    { model | rules = list }


addRule : Rule -> Repo -> Repo
addRule rule model =
    let
        ruleInfo =
            { groupId = rule.groupId
            , name = rule.name
            , description = rule.description
            , language = rule.language
            , id = rule.id
            , creationTime = rule.creationTime
            , createdBy = rule.createdBy
            , lastUpdateTime = rule.lastUpdateTime
            , lastUpdatedBy = rule.lastUpdatedBy
            , ruleCode = rule.ruleCode
            , mappedCode = rule.mappedCode
            , inverse = rule.inverse
            , severity = rule.severity
            , url = rule.url
            , enabled = rule.enabled
            , message = rule.message
            }

        rules =
            ruleInfo :: List.filter (\t -> t.id /= rule.id) model.rules
    in
    { model | rules = rules }


deleteRule : String -> Repo -> Repo
deleteRule ruleId model =
    { model
    | rules = List.filter (\r -> r.id /= ruleId) model.rules
    , view = ViewGroup (getSelectedGroup model)
    }



-- Versioning


setVersions : List RuleVersion -> Repo -> Repo
setVersions versions model =
    { model | versions = versions }


getVersions : Repo -> List RuleVersion
getVersions model =
    model.versions
        |> List.sortBy .version
        |> List.reverse



-- Schema

fieldTypes =
    [ ( "String", "WORD" )
    , ( "Boolean", "BOOLEAN" )
    , ( "Number", "NUMBER" )
    , ( "Date", "DATE" )
    , ( "", "NULL" )
    ]

getPrettyFieldType: String -> Repo -> String
getPrettyFieldType type_ repo =
    fieldTypes
        |> List.filter (\(_,v) -> v == type_)
        |> List.map (\(d,_) -> d)
        |> List.head
        |> Maybe.withDefault ""

getSchema : Repo -> Schema
getSchema repo =
    repo.schema


setSchema : Schema -> Repo -> Repo
setSchema schema repo =
    { repo | schema = schema }


getSchemaFields : Repo -> List ( String, String, String )
getSchemaFields repo =
    let
        mapDs dsName cols =
            cols
                |> List.map (\col -> ( dsName ++ "." ++ col.name, col.type_, col.description ))
    in
    repo.schema.dataSets
        |> List.map (\ds -> mapDs ds.name ds.columns)
        |> List.concat
        |> List.sortBy (\( n, _, _ ) -> n)

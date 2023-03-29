#!/usr/bin/env bash

##### VARIABLE
token=""
id_project=""
ref=""
tag_name=""
message=""

##### FUNCTION
help() {
  echo "поддерживаемые флаги:"
  echo "-token, --token - token с правами на запись в GitLab"
  echo "-id, --project_id - ID проекта в Gitlab (CI_PROJECT_ID)"
  echo "-r, --ref - имя ветки, SHA коммита либо TAG"
  echo "-tag, --tag_name - имя метки"
  echo "-m, --message - описание для метки"
  echo "смотри https://docs.gitlab.com/ee/api/tags.html"

}

while [ "$1" != "" ]; do
  case $1 in
  -token | --token)
    shift
    token=$1
    ;;
  -id | --project_id)
    shift
    id_project=$1
    ;;
  -r | --ref)
    shift
    ref=$1
    ;;
  -tag | --tag_name)
    shift
    tag_name=$1
    ;;
  -m | --message)
    shift
    message=$1
    ;;
  -h | --help)
    help
    exit
    ;;
  esac
  shift
done

echo id_project: "$id_project"
echo ref: "$ref"
echo tag_name: "$tag_name"
echo message: "$message"
echo token: "$token"

if [ "$token" == "" ]; then
  echo "token не установлен. Пожалуйста обратитесь к владельцу репозитория или администратору"
  exit 1
elif [ "$id_project" == "" ]; then
  echo "ID проекта в Gitlab (CI_PROJECT_ID) не установлен"
  exit 1
elif [ "$ref" == "" ]; then
  echo "Имя ветки не установлено"
  exit 1
elif [ "$tag_name" == "" ]; then
  echo "Метка не установлена"
  exit 1
fi

if [ "$RootCA" != "" ]; then
  echo "$RootCA" >|./rootCA.crt
fi
curl -v --cacert ./rootCA.crt --request POST --header "PRIVATE-TOKEN: $token" "https://gitlab.tools.russianpost.ru/api/v4/projects/$id_project/repository/tags?tag_name=$tag_name&ref=$ref&message=$message"

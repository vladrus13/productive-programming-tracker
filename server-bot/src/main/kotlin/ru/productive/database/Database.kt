package ru.productive.database

interface Database {
  fun getUsers() : List<String>
}
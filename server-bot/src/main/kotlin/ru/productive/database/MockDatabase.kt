package ru.productive.database

import kotlin.io.path.Path
import kotlin.io.path.readLines

class MockDatabase : Database {

  private val users = Path(MockDatabase::class.java.getResource("/students.txt")!!.path).readLines()
  override fun getUsers(): List<String> = users
}
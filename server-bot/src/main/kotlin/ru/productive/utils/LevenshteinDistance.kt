package ru.productive.utils

import kotlin.math.min

/**
 * Levenshtein distance
 *
 * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>
 */
class LevenshteinDistance {
  companion object {
    /**
     * Calculate Levenshtein distance between two string
     */
    fun calculate(s1 : String, s2 : String): Int {
      val dp = Array(s1.length + 1){ IntArray(s2.length + 1) { 0 } }
      (0..s1.length).forEach { i ->
        (0..s2.length).forEach { j ->
          if (i == 0 || j == 0) {
            dp[i][j] = i + j
          } else {
            val mistake = if (s1[i - 1] == s2[j - 1]) 0 else 1
            dp[i][j] = arrayOf(dp[i - 1][j - 1] + mistake, dp[i - 1][j] + 1, dp[i][j - 1] + 1).min()
          }
        }
      }
      return dp[s1.length][s2.length]
    }

    /**
     * Calculate the optimal distance between two collections of strings. We find the best permutation of string
     */
    fun calculate(s1: List<String>, s2: List<String>): Int {
      // we can use kuhn maximal matching algorithm, but there are small lists
      var min = s1.sumOf { it.length } + s2.sumOf { it.length }

      fun rec(accumulator : MutableList<String>, used : MutableList<Boolean>) {
        if (accumulator.size == s2.size || accumulator.size == s1.size) {
          var result = 0
          (0 until (min(s1.size, s2.size))).forEach { p -> result += calculate(accumulator[p], s2[p]) }
          min = min(min, result)
        } else {
          (s1.indices).forEach { i ->
            if (!used[i]) {
              used[i] = true
              accumulator.add(s1[i])
              rec(accumulator, used)
              accumulator.removeLast()
              used[i] = false
            }
          }
        }
      }

      rec(mutableListOf(), MutableList(s1.size) { false })
      return min
    }
  }
}
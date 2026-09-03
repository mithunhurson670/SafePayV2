package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )

    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    fun amountInWords(amountLong: Long): String {
        if (amountLong <= 0) return "Zero rupees"
        if (amountLong > 999999999) return "Amount very large"

        val sb = StringBuilder()
        var n = amountLong

        // Crores
        if (n >= 10000000) {
            val cr = n / 10000000
            sb.append(convertThreeDigits(cr)).append(" crore ")
            n %= 10000000
        }

        // Lakhs
        if (n >= 100000) {
            val lk = n / 100000
            sb.append(convertThreeDigits(lk)).append(" lakh ")
            n %= 100000
        }

        // Thousands
        if (n >= 1000) {
            val th = n / 1000
            sb.append(convertThreeDigits(th)).append(" thousand ")
            n %= 1000
        }

        // Hundreds and remaining
        if (n > 0) {
            sb.append(convertThreeDigits(n)).append(" ")
        }

        val clean = sb.toString().trim().replace(Regex("\\s+"), " ")
        val lower = clean.lowercase(Locale.getDefault())
        val formatted = lower.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return "$formatted rupees"
    }

    private fun convertThreeDigits(num: Long): String {
        var n = num
        val sb = StringBuilder()

        if (n >= 100) {
            val h = (n / 100).toInt()
            sb.append(units[h]).append(" hundred ")
            n %= 100
        }

        if (n >= 20) {
            val t = (n / 10).toInt()
            val u = (n % 10).toInt()
            sb.append(tens[t])
            if (u > 0) {
                sb.append(" ").append(units[u])
            }
        } else if (n > 0) {
            sb.append(units[n.toInt()])
        }

        return sb.toString().trim()
    }

    fun formatCurrency(amount: Double): String {
        val longVal = amount.toLong()
        return if (amount == longVal.toDouble()) {
            "₹%,d".format(Locale.getDefault(), longVal)
        } else {
            "₹%,.2f".format(Locale.getDefault(), amount)
        }
    }

    fun formatPlainDateTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val oneDay = 24 * 60 * 60 * 1000L

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date(timestamp))

        return when {
            diff < 2 * 60 * 1000L -> "Just a moment ago"
            diff < oneDay -> "Today at $timeStr"
            diff < 2 * oneDay -> "Yesterday at $timeStr"
            else -> {
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }
}

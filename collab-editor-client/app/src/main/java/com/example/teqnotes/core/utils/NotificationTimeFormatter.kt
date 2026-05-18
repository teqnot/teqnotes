package com.example.teqnotes.core.utils

object NotificationTimeFormatter {

    fun format(timestamp: Long): String {

        val diff =
            System.currentTimeMillis() - timestamp

        val minutes = diff / 1000 / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {

            minutes < 60 ->
                "${minutes}m"

            hours < 24 ->
                "${hours}h"

            days < 7 ->
                "${days}d"

            weeks < 4 ->
                "${weeks}w"

            months < 12 ->
                "${months}mo"

            else ->
                "${years}y"
        }
    }
}
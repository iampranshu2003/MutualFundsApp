package com.example.mutualfundsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long
)
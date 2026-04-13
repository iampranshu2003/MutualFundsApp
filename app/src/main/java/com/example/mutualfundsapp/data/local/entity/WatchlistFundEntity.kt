package com.example.mutualfundsapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchlist_funds",
    foreignKeys = [
        ForeignKey(
            entity = WatchlistEntity::class,
            parentColumns = ["id"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("watchlistId")]
)
data class WatchlistFundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val watchlistId: String,
    val schemeCode: String,
    val schemeName: String,
    val nav: String
)

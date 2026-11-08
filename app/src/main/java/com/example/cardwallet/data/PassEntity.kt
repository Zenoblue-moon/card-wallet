package com.example.cardwallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passes")
data class PassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val subtitle: String = "",
    val organizationName: String = "",
    val backgroundColor: Int = 0xFF1B1B1F.toInt(),
    val foregroundColor: Int = 0xFFFFFFFF.toInt(),
    val labelColor: Int = 0xFF999999.toInt(),
    val barcodeMessage: String = "",
    val barcodeFormat: String = "",
    val iconPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
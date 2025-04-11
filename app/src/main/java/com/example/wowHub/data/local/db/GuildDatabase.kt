package com.example.wowHub.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wowHub.data.local.db.entities.*

@Database(entities = [GuildMember::class, Message::class, GuildEvent::class, Report::class, WoWAuditMember::class], version = 2)
abstract class GuildDatabase : RoomDatabase() {
    abstract fun guildDao(): GuildDao
}
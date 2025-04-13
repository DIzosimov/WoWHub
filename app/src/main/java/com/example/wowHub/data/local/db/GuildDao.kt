package com.example.wowHub.data.local.db

import androidx.room.*
import com.example.wowHub.data.local.db.entities.*

@Dao
interface GuildDao {
    @Query("SELECT * FROM GuildMember")
    suspend fun getAllMembers(): List<GuildMember>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: GuildMember)

    @Delete
    suspend fun deleteMember(member: GuildMember)

    @Query("SELECT * FROM Message ORDER BY timestamp DESC")
    suspend fun getAllMessages(): List<Message>

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM GuildEvent ORDER BY eventDate ASC")
    suspend fun getAllEvents(): List<GuildEvent>

    @Insert
    suspend fun insertEvent(event: GuildEvent)

    @Delete
    suspend fun deleteEvent(event: GuildEvent)

    @Query("SELECT * FROM Report")
    suspend fun getAllReports(): List<Report>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<Report>)

    @Query("SELECT * FROM WoWAuditMember")
    suspend fun getAllWoWAuditMembers(): List<WoWAuditMember>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWoWAuditMembers(members: List<WoWAuditMember>)
}
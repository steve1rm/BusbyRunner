package me.androidbox.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Deleted a run remotely that failed
 *  But was deleted from the DB, so the BE would never
 *  know that the run was deleted locally
 * */
@Entity
class DeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String)
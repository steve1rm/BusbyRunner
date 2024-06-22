package me.androidbox.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Logging out will always delete all runs from the device
 *
 * When logging before the runs have been deleted from the backend
 * when there no internet add these to this entity.
 * So when the internet becomes available they will be deleted
 * from the backend */
@Entity
class RunPendingSyncEntity(
    @Embedded val run: RunEntity,
    @PrimaryKey(autoGenerate = false)
    val runId: String = run.id,
    val mapPictureBytes: ByteArray,
    val userId: String
) {
    /**
     * We need this so we can compare different runPendingSyncEntity's */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunPendingSyncEntity

        if (run != other.run) return false
        if (runId != other.runId) return false
        /**
         * Compare the contents of this byte array instead of its reference */
        if (!mapPictureBytes.contentEquals(other.mapPictureBytes)) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = run.hashCode()
        result = 31 * result + runId.hashCode()
        result = 31 * result + mapPictureBytes.contentHashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}

package dev.ozodn.onlineshop.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Extends {@link BaseEntity} to provide soft deletion support via a deletion timestamp.
 */
@MappedSuperclass
@Getter @Setter
public abstract class SoftDeletableEntity extends BaseEntity {

    private Instant deletedAt;

    /**
     * Checks whether this entity has been soft-deleted.
     *
     * @return {@code true} if deleted, {@code false} otherwise
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Marks this entity as deleted by recording the current timestamp.
     */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
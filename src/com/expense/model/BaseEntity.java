package com.expense.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.Timestamp;

/**
 * Base abstract entity containing common id and createdAt fields.
 */
public abstract class BaseEntity {
    protected Integer id; // nullable until persisted
    protected LocalDateTime createdAt;

    public BaseEntity() {
    }

    public BaseEntity(Integer id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setCreatedAtFromTimestamp(Timestamp ts) {
        if (ts != null) {
            this.createdAt = LocalDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
        }
    }
}

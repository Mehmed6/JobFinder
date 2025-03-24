package com.doganmehmet.app.repository;

import com.doganmehmet.app.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILogEntryRepository extends JpaRepository<LogEntry, Long> {
}

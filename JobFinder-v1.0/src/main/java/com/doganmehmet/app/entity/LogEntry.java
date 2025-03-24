package com.doganmehmet.app.entity;

import com.doganmehmet.app.enums.LogType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@Table(name = "log_entries")
@NoArgsConstructor
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    @Column(name = "performed_by")
    private String performedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type")
    private LogType logType;
    private String timestamp;

    public LogEntry(String performedBy, String action, LogType logType)
    {
        this.performedBy = performedBy;
        this.action = action;
        this.logType = logType;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

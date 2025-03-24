package com.doganmehmet.app.service;

import com.doganmehmet.app.entity.LogEntry;
import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.repository.ILogEntryRepository;
import org.springframework.stereotype.Service;

@Service
public class LogEntryService {
    private final ILogEntryRepository m_logEntryRepository;

    public LogEntryService(ILogEntryRepository logEntryRepository)
    {
        m_logEntryRepository = logEntryRepository;
    }

    public void logger(String performedBy, String message, LogType logType)
    {
        m_logEntryRepository.save(new LogEntry(performedBy, message, logType));
    }
}

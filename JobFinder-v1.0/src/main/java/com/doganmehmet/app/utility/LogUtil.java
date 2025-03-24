package com.doganmehmet.app.utility;

import com.doganmehmet.app.enums.LogType;
import com.doganmehmet.app.service.LogEntryService;

public class LogUtil {

    public static void log(String performedBy, String action, LogType logType)
    {
        var logEntryService = ApplicationContextProvider.getBean(LogEntryService.class);
        logEntryService.logger(performedBy, action, logType);
    }
}

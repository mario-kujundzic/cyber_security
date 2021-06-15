package com.security.hospital.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.security.hospital.dto.LogMessageDTO;
import com.security.hospital.enums.LogMessageType;
import com.security.hospital.model.Log;
import com.security.hospital.repository.CyberLogRepository;
import com.security.hospital.util.FileUtility;

@Service
public class LogService {
	
	@Autowired
	private CyberLogRepository logRepository;

    private final static String logsFolderPath = "logs/";

    private void logMessage(String source, LogMessageType type, String content) {
        logMessage(source, type, content, null);
        
    }

    private void logMessage(String source, LogMessageType type, String content, Map<String, Object> params) {
    	long unixMilis= (new Date()).getTime();
        LogMessageDTO message = new LogMessageDTO(unixMilis, type, content, params);
        String logLine = message.toString();

        System.out.println(source + " -> " + logLine);
        String filePath = logsFolderPath + source + ".log";
        
        Log cyberLog = new Log(message);
        cyberLog.setId(getNextId());
        
        logRepository.save(cyberLog);

        try {
            tryCreateFolder();
        } catch (IOException e) {
            System.out.println("LogService ERROR: Can't create log folder" + e.getMessage());
        }

        try {
            FileUtility.appendLine(logsFolderPath + source + ".log", logLine);
        } catch (IOException e) {
            System.out.println("LogService ERROR: Can't write log line '" + logLine + "' to file '" + filePath + "': " + e.getMessage());
        }
    }

    public String[] findLogSources() throws IOException {
        ArrayList<String> sources = new ArrayList<>();

        tryCreateFolder();

        File directory = new File(logsFolderPath);
        for (File file : directory.listFiles()) {
            if (!file.getName().endsWith(".log")) {
                continue;
            }

            int trimTo = file.getName().lastIndexOf(".log");
            String sourceName = file.getName().substring(0, trimTo);
            sources.add(sourceName);
        }

        return sources.toArray(new String[0]);
    }

    public HashMap<String, ArrayList<LogMessageDTO>> loadLogLinesSince(long minUnixMilis) throws Exception {
        HashMap<String, ArrayList<LogMessageDTO>> logLinesMap = new HashMap<>();
        tryCreateFolder();

        File directory = new File(logsFolderPath);
        for (File file : directory.listFiles()) {
            if (!file.getName().endsWith(".log")) {
                continue;
            }

            ArrayList<LogMessageDTO> messages = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                LogMessageDTO logMessage = LogMessageDTO.fromString(line);
                if (logMessage.getUnixMilis() > minUnixMilis) {
                    messages.add(logMessage);
                }
            }

            int trimTo = file.getName().lastIndexOf(".log");
            String sourceName = file.getName().substring(0, trimTo);

            logLinesMap.put(sourceName, messages);
        }

        return logLinesMap;
    }

    private void tryCreateFolder() throws IOException {
        if (!FileUtility.folderExists(logsFolderPath)) {
            FileUtility.createFolder(logsFolderPath);
        }
    }

    public void logGeneralInfo(String message) {
        logMessage("general", LogMessageType.INFO, message);
    }

    public void logGeneralWarning(String message) {
        logMessage("general", LogMessageType.WARNING, message);
    }

    public void logGeneralError(String message) {
        logMessage("general", LogMessageType.ERROR, message);
    }

    public void logAuthInfo(String message) {
        logMessage("auth", LogMessageType.INFO, message);
    }

    public void logAuthWarning(String message) {
        logMessage("auth", LogMessageType.WARNING, message);
    }

    public void logAuthError(String message) {
        logMessage("auth", LogMessageType.ERROR, message);
    }

    public void logDeviceInfo(String deviceId, String message) {
        logMessage("device_" + deviceId, LogMessageType.INFO, message);
    }
    
    public void logDeviceInfo(String deviceId, String message, Map<String, Object> params) {
        logMessage("device_" + deviceId, LogMessageType.INFO, message, params);
    }

    public void logDeviceWarning(String deviceId, String message) {
        logMessage("device_" + deviceId, LogMessageType.WARNING, message);
    }

    public void logDeviceError(String deviceId, String message) {
        logMessage("device_" + deviceId, LogMessageType.ERROR, message);
    }
    
    @Autowired private MongoOperations mongo;

    public int getNextId()
    {
        CustomSequences counter = mongo.findAndModify(
            query(where("_id").is("customSequences")),
            new Update().inc("seq",1),
            options().returnNew(true).upsert(true),
            CustomSequences.class);
        return counter.getSeq();
    }
    
    @Document(collection = "customSequences")
    public class CustomSequences {
        @Id
        private String id;
        private int seq;

    	public int getSeq() {
    		return seq;
    	}
    }

}

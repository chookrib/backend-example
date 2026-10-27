package com.example.backend.adapter.driving;

import com.example.backend.Application;
import com.example.backend.utility.ValueUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Well Known Controller
 */
@Controller
public class WellKnownController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    private static final LocalDateTime startTime = LocalDateTime.now();

    /**
     * 应用信息，显示非涉密信息
     */
    @RequestMapping(value = "/.well-known/metadata", method = RequestMethod.GET,
            // produces = MediaType.TEXT_PLAIN_VALUE   // text/plain;charset=ISO-8859-1
            produces = "text/plain;charset=UTF-8"
    )
    @ResponseBody
    public String metadata() {
        Properties props = Application.getManifestProperties();
        return "File-Name: " + Application.getFileName() +
                System.lineSeparator() +
                "Build-Time: " + props.getProperty("Build-Time", "") +
                System.lineSeparator() +
                "Git-Commit-Id-Abbrev: " + props.getProperty("Git-Commit-Id-Abbrev", "") +
                System.lineSeparator() +
                "Start-Time: " + ValueUtility.formatDateTime(startTime) +
                System.lineSeparator();
    }

    /**
     * 应用信息，显示非涉密信息，JSON格式
     */
    @RequestMapping(value = "/.well-known/metadata.json", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> metadataJson() {
        Properties props = Application.getManifestProperties();
        Map<String, Object> response = new HashMap<>();
        response.put("fileName", Application.getFileName());
        response.put("buildTime", props.getProperty("Build-Time", ""));
        response.put("gitCommitIdAbbrev", props.getProperty("Git-Commit-Id-Abbrev", ""));
        response.put("startTime", ValueUtility.formatDateTime(startTime));
        return response;
    }
}



package com.example.ddd.adapter.driving;

import com.example.ddd.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;

/**
 * Well Known Controller
 */
@Controller
public class WellKnownController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    /**
     * 应用信息，显示非涉密信息
     */
    @RequestMapping(value = "/.well-known/info", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String info() {
        Properties props = Application.getManifestProperties();
        return "File-Name: " + Application.getFileName() +
               System.lineSeparator() +
               "Build-Time: " + props.getProperty("Build-Time", "") +
               System.lineSeparator() +
               "Git-Commit-Id-Abbrev: " + props.getProperty("Git-Commit-Id-Abbrev", "") +
               System.lineSeparator();
    }
}



package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.Result;
import com.example.backend.utility.JsonExtractor;
import com.example.backend.utility.JsonUtility;
import com.example.backend.utility.ValueUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * JSON 测试 Controller
 */
@RestController
public class TestJsonController {

    private static final Logger logger = LoggerFactory.getLogger(TestJsonController.class);

    @RequestMapping(value = "/api/test/json/basic", method = RequestMethod.GET)
    public Result testJsonBasic() {
        return Result.okData(
                new ValueUtility.LinkedHashMapBuilder<String, Object>()
                        .put("serializeEmpty", JsonUtility.serialize(""))
                        .put("serializeNull", JsonUtility.serialize(null))
                        .put("deserializeEmpty", JsonUtility.deserialize(""))
                        .put("deserializeNull", JsonUtility.deserialize("null"))
                        .build()
        );
    }

    @RequestMapping(value = "/api/test/json/extractor", method = RequestMethod.GET)
    public Result testJsonExtractor() {
        String jsonString = """
                {
                    "1": {
                        "1_1": "1-1",
                        "1_2": "1-2"
                    },
                    "2":{
                        "2_1": 21,
                        "2_2": 22
                    },
                    "3": ["31", 32]
                }
                """;
        JsonExtractor jsonExtractor = new JsonExtractor(jsonString);

        return Result.okData(
                new ValueUtility.LinkedHashMapBuilder<String, Object>()
                        .put("test1", jsonExtractor.getString("1"))
                        .put("test2", jsonExtractor.getString("1.1_1"))
                        .put("test3", jsonExtractor.getStringOrEmpty("1.1_1.1_1_1"))
                        .put("test4", jsonExtractor.getString("2.2_1"))
                        .put("test5", jsonExtractor.getStringList("3"))
                        .build()
                // Map.of(
                //         "test1", jsonExtractor.getString("1"),
                //         "test2", jsonExtractor.getString("1.1_1"),
                //         "test3", jsonExtractor.getStringOrEmpty("1.1_1.1_1_1"),
                //         "test4", jsonExtractor.getString("2.2_1")
                //         )
        );
    }

}

package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.Result;
import com.example.backend.utility.IdentifierUtility;
import com.example.backend.utility.ValueUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Identifier 测试 Controller
 */
@RestController
public class TestIdentifierController {

    private static final Logger logger = LoggerFactory.getLogger(TestIdentifierController.class);

    @RequestMapping(value = "/api/test/id", method = RequestMethod.GET)
    public Result testId() {
        var map = new ValueUtility.LinkedHashMapBuilder<String, Object>()
                .put("join", new ValueUtility.LinkedHashMapBuilder<String, String>()
                        .put("e0", IdentifierUtility.wrapJoin(List.of()))
                        .put("e1", IdentifierUtility.wrapJoin(List.of("")))
                        .put("e2", IdentifierUtility.wrapJoin(List.of("", "")))
                        .put("e3", IdentifierUtility.wrapJoin(List.of("", "", "")))
                        .put("v1", IdentifierUtility.wrapJoin(List.of("a")))
                        .put("v2", IdentifierUtility.wrapJoin(List.of("a", "b")))
                        .put("v3", IdentifierUtility.wrapJoin(List.of("a", "b", "c")))
                        .build()
                )
                .put("split", new ValueUtility.LinkedHashMapBuilder<String, Object>()
                        .put("e0", IdentifierUtility.split(""))
                        .put("e1", IdentifierUtility.split(",,"))
                        .put("e2", IdentifierUtility.split(",,,"))
                        .put("e3", IdentifierUtility.split(",,,,"))
                        .put("v1", IdentifierUtility.split(",a,"))
                        .put("v2", IdentifierUtility.split(",a,b,"))
                        .put("v3", IdentifierUtility.split(",a,b,c,"))
                        .build()
                )
                .build();
        return Result.okData(
                // Map.of(
                //         "join", Map.of(
                //                 "e0", IdentifierUtility.wrapJoin(List.of()),
                //                 "e1", IdentifierUtility.wrapJoin(List.of("")),
                //                 "e2", IdentifierUtility.wrapJoin(List.of("", "")),
                //                 "e3", IdentifierUtility.wrapJoin(List.of("", "", "")),
                //                 "v1", IdentifierUtility.wrapJoin(List.of("a")),
                //                 "v2", IdentifierUtility.wrapJoin(List.of("a", "b")),
                //                 "v3", IdentifierUtility.wrapJoin(List.of("a", "b", "c"))
                //         ),
                //         "split", Map.of(
                //                 "e0", IdentifierUtility.split(""),
                //                 "e1", IdentifierUtility.split(",,"),
                //                 "e2", IdentifierUtility.split(",,,"),
                //                 "e3", IdentifierUtility.split(",,,,"),
                //                 "v1", IdentifierUtility.split(",a,"),
                //                 "v2", IdentifierUtility.split(",a,b,"),
                //                 "v3", IdentifierUtility.split(",a,b,c,")
                //         )
                // )
                map
        );
    }
}

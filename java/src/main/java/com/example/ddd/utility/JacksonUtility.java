package com.example.ddd.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Jackson Utility
 */
public class JacksonUtility {

    /**
     * 读取JsonNode
     */
    public static JsonNode readTree(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析JSON字符串异常", e);
        }
    }

    /**
     * 将对象转换为Json字符串
     */
    public static String writeValueAsString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("生成JSON字符串异常", e);
        }
    }

    ///**
    // * 将对象转换为Map
    // */
    //public static Map<String, ?> convertValue(Object object) {
    //    //try {
    //        return new ObjectMapper()
    //                //.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    //                .convertValue(object, Map.class);
    //    //} catch (Exception e) {
    //    //    throw new RuntimeException("对象转为Map异常", e);
    //    //}
    //}

    // =================================================================================================================
    // 以下为用于JSON转换测试的数据定义

    /**
     * 测试类
     */
    public class TestClass {

        private String stringNull = null;
        private String stringEmpty = "";
        private String stringTest = "test";

        private boolean booleanTrue = true;
        private boolean booleanFalse = false;

        private Boolean booleanClassNull = null;
        private Boolean booleanClassTrue = Boolean.TRUE;
        private Boolean booleanClassFalse = Boolean.FALSE;

        private int intZero = 0;
        private int intMin = Integer.MIN_VALUE;
        private int intMax = Integer.MAX_VALUE;

        private Integer intClassNull = null;
        private Integer intClassZero = 0;
        private Integer intClassMin = Integer.MIN_VALUE;
        private Integer intClassMax = Integer.MAX_VALUE;

        private long longZero = 0;
        private long longMin = Long.MIN_VALUE;
        private long longMax = Long.MAX_VALUE;

        private Long longClassNull = null;
        private Long longClassZero = 0L;
        private Long longClassMin = Long.MIN_VALUE;
        private Long longClassMax = Long.MAX_VALUE;

        private BigDecimal bigDecimalNull = null;
        private BigDecimal bigDecimalMin = BigDecimal.valueOf(Double.MIN_VALUE);
        private BigDecimal bigDecimalMax = BigDecimal.valueOf(Double.MAX_VALUE);

        private Date dateNull = null;
        private Date dateNow = new Date();
        private Date dateMin = new Date(0L);
        private Date dateMax = new Date(Long.MAX_VALUE);

        private Date dateNullDate = null;
        private Date dateNowDate = new Date();
        private Date dateMinDate = new Date(0L);
        private Date dateMaxDate = new Date(Long.MAX_VALUE);

        private TestEnum enumNull = null;
        private TestEnum enum1 = TestEnum.TEST_ENUM_1;
        private TestEnum enum2 = TestEnum.TEST_ENUM_2;
        private TestEnum enum3 = TestEnum.TEST_ENUM_3;

        public String getStringTest() { return stringTest; }

        public String getStringNull() { return stringNull; }

        public String getStringEmpty() { return stringEmpty; }

        public boolean isBooleanTrue() { return booleanTrue; }

        public boolean isBooleanFalse() { return booleanFalse; }

        public Boolean getBooleanClassNull() { return booleanClassNull; }

        public Boolean getBooleanClassTrue() { return booleanClassTrue; }

        public Boolean getBooleanClassFalse() { return booleanClassFalse; }

        public int getIntZero() { return intZero; }

        public int getIntMin() { return intMin; }

        public int getIntMax() { return intMax; }

        public Integer getIntClassNull() { return intClassNull; }

        public Integer getIntClassZero() { return intClassZero; }

        public Integer getIntClassMin() { return intClassMin; }

        public Integer getIntClassMax() { return intClassMax; }

        public long getLongZero() { return longZero; }

        public long getLongMin() { return longMin; }

        public long getLongMax() { return longMax; }

        public Long getLongClassNull() { return longClassNull; }

        public Long getLongClassZero() { return longClassZero; }

        public Long getLongClassMin() { return longClassMin; }

        public Long getLongClassMax() { return longClassMax; }

        public BigDecimal getBigDecimalNull() { return bigDecimalNull; }

        public BigDecimal getBigDecimalMin() { return bigDecimalMin; }

        public BigDecimal getBigDecimalMax() { return bigDecimalMax; }

        public Date getDateNull() { return dateNull; }

        public Date getDateNow() { return dateNow; }

        public Date getDateMin() { return dateMin; }

        public Date getDateMax() { return dateMax; }

        public Date getDateNullDate() { return dateNullDate; }

        public Date getDateNowDate() { return dateNowDate; }

        public Date getDateMinDate() { return dateMinDate; }

        public Date getDateMaxDate() { return dateMaxDate; }

        public TestEnum getEnumNull() { return enumNull; }

        public TestEnum getEnum1() { return enum1; }

        public TestEnum getEnum2() { return enum2; }

        public TestEnum getEnum3() { return enum3; }
    }

    /**
     * 测试类引用
     */
    public class TestClassRef {

        private TestClass cls = new TestClass();
        private ArrayList<TestClass> arrayList = new ArrayList<>();
        private Map<String, Object> map = new HashMap<>();

        public TestClassRef() {
            arrayList.add(new TestClass());
            map.put("mapKey", new TestClass());
        }

        public TestClass getCls() { return cls; }

        public ArrayList<TestClass> getArrayList() { return arrayList; }

        public Map<String, Object> getMap() { return map; }
    }


    /**
     * 测试枚举
     */
    public enum TestEnum {

        TEST_ENUM_1(1, "枚举1"),
        TEST_ENUM_2(2, "枚举2"),
        TEST_ENUM_3(3, "枚举3");

        private int value;      //值
        private String text;    //文本

        public int getValue() { return value; }

        public String getText() { return text; }

        TestEnum(int value, String text) {
            this.value = value;
            this.text = text;
        }

        /**
         * 根据值取枚举，找不到返回null
         */
        public static TestEnum getByValue(int value) {
            for (TestEnum item : TestEnum.values()) {
                if (item.value == value) {
                    return item;
                }
            }
            return null;
        }
    }
}

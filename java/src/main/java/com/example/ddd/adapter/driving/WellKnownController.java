package com.example.ddd.adapter.driving;

import com.example.ddd.Application;
import com.example.ddd.application.lock.TestLockService;
import com.example.ddd.utility.JsonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        return "File-Name: " + Application.getFileName() +
               System.lineSeparator() +
               "Build-Time: " + Application.getBuildTime() +
               System.lineSeparator();
    }

    //==================================================================================================================
    // 测试异常

    /**
     * 测试异常处理
     */
    @RequestMapping(value = "/.well-known/test/exception", method = RequestMethod.GET)
    @ResponseBody
    public Result testException() {
        throw new RuntimeException("测试异常");
    }

    //==================================================================================================================
    // 测试JSON转换

    /**
     * 测试 JSON 数据输出
     */
    @RequestMapping(value = "/.well-known/test/json/class", method = RequestMethod.GET)
    @ResponseBody
    public Result testJsonClass() {
        TestDataClass c = new TestDataClass();
        return Result.okData(c);
    }

    /**
     * 测试 JSON 数据输出
     */
    @RequestMapping(value = "/.well-known/test/json/class-complex", method = RequestMethod.GET)
    @ResponseBody
    public Result testJsonComplexClass() {
        TestDataComplexClass c = new TestDataComplexClass();
        return Result.okData(c);
    }

    //==================================================================================================================
    // 测试锁

    private final TestLockService testLockService;

    public WellKnownController(TestLockService testLockService) {
        this.testLockService = testLockService;
    }

    /**
     * 扣减测试，不加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/reduce-unsafe", method = RequestMethod.GET)
    @ResponseBody
    public Result testLockReduceUnsafe() {
        this.testLockService.reduceUnsafe();
        return Result.ok();
    }

    /**
     * 扣减测试，加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/reduce-safe", method = RequestMethod.GET)
    @ResponseBody
    public Result testLockReduceSafe() {
        this.testLockService.reduceSafe();
        return Result.ok();
    }

    /**
     * 加锁等待测试
     */
    @RequestMapping(value = "/.well-known/test/lock/sleep", method = RequestMethod.GET)
    @ResponseBody
    public Result testLockSleep() {
        this.testLockService.lockSleep();
        return Result.ok();
    }

    // =================================================================================================================
    // 以下为用于JSON转换测试的数据定义

    /**
     * 用于测试 JSON 转换的数据类
     */
    public class TestDataClass {

        private Object objectNull = null;
        //private Object objectNew = new Object();

        private String stringNull = null;
        private String stringEmpty = "";
        private String stringTest = "test";

        private boolean booleanTrue = true;
        private boolean booleanFalse = false;

        private Boolean booleanClassNull = null;
        private Boolean booleanClassTrue = Boolean.TRUE;
        private Boolean booleanClassFalse = Boolean.FALSE;

        private int intZero = 0;
        private int intMin = Integer.MIN_VALUE;     //-2147483648
        private int intMax = Integer.MAX_VALUE;     //2147483647

        private Integer intClassNull = null;
        private Integer intClassZero = 0;
        private Integer intClassMin = Integer.MIN_VALUE;    //-2147483648
        private Integer intClassMax = Integer.MAX_VALUE;    //2147483647

        private long longZero = 0;
        private long longMin = Long.MIN_VALUE;      //-9223372036854775808
        private long longMax = Long.MAX_VALUE;      //9223372036854775807

        private Long longClassNull = null;
        private Long longClassZero = 0L;
        private Long longClassMin = Long.MIN_VALUE;     //-9223372036854775808
        private Long longClassMax = Long.MAX_VALUE;     //9223372036854775807

        private BigDecimal bigDecimalNull = null;
        private BigDecimal bigDecimalMin = BigDecimal.valueOf(Double.MIN_VALUE);    //5e-324    "4.9E-324"
        private BigDecimal bigDecimalMax = BigDecimal.valueOf(Double.MAX_VALUE);    //1.7976931348623157e+308   "1.7976931348623157E+308"

        private Date dateNull = null;
        private Date dateNow = new Date();
        private Date dateMin = new Date(0L);            //0     "1970-01-01 08:00:00"
        private Date dateMax = new Date(Long.MAX_VALUE);    //9.223372036854776e+18     "292278994-08-17 15:12:55"

        private LocalDateTime localDateTimeNull = null;
        private LocalDateTime localDateTimeNow = LocalDateTime.now();
        private LocalDateTime localDateTimeMin = LocalDateTime.MIN;     //"-999999999-01-01T00:00:00"
        private LocalDateTime localDateTimeMax = LocalDateTime.MAX;     //"+999999999-12-31T23:59:59.999999999"

        private LocalDate localDateNull = null;
        private LocalDate localDateNow = LocalDate.now();
        private LocalDate localDateMin = LocalDate.MIN;         //"-999999999-01-01"
        private LocalDate localDateMax = LocalDate.MAX;         //"+999999999-12-31"

        private LocalTime localTimeNull = null;
        private LocalTime localTimeNow = LocalTime.now();
        private LocalTime localTimeMin = LocalTime.MIN;     //"00:00:00"
        private LocalTime localTimeMax = LocalTime.MAX;     //"23:59:59.999999999"

        private TestDataEnum enumNull = null;
        private TestDataEnum enum1 = TestDataEnum.ENUM_1;
        private TestDataEnum enum2 = TestDataEnum.ENUM_2;

        public Object getObjectNull() { return objectNull; }

        //public Object getObjectNew() { return objectNew; }

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

        public LocalDateTime getLocalDateTimeNull() { return localDateTimeNull; }

        public LocalDateTime getLocalDateTimeNow() { return localDateTimeNow; }

        public LocalDateTime getLocalDateTimeMin() { return localDateTimeMin; }

        public LocalDateTime getLocalDateTimeMax() { return localDateTimeMax; }

        public LocalDate getLocalDateNull() { return localDateNull; }

        public LocalDate getLocalDateNow() { return localDateNow; }

        public LocalDate getLocalDateMin() { return localDateMin; }

        public LocalDate getLocalDateMax() { return localDateMax; }

        public LocalTime getLocalTimeNull() { return localTimeNull; }

        public LocalTime getLocalTimeNow() { return localTimeNow; }

        public LocalTime getLocalTimeMin() { return localTimeMin; }

        public LocalTime getLocalTimeMax() { return localTimeMax; }

        public TestDataEnum getEnumNull() { return enumNull; }

        public TestDataEnum getEnum1() { return enum1; }

        public TestDataEnum getEnum2() { return enum2; }
    }

    /**
     * 用于测试 JSON 转换的数据类
     */
    public class TestDataComplexClass {

        private TestDataClass cls = new TestDataClass();
        private ArrayList<TestDataClass> list = new ArrayList<>();
        private Map<String, Object> map = new HashMap<>();

        public TestDataComplexClass() {
            list.add(new TestDataClass());
            map.put("a", new TestDataClass());
        }

        public TestDataClass getCls() { return cls; }

        public ArrayList<TestDataClass> getList() { return list; }

        public Map<String, Object> getMap() { return map; }
    }


    /**
     * 用于测试 JSON 转换的枚举
     */
    public enum TestDataEnum {

        ENUM_1(1, "枚举1"),
        ENUM_2(2, "枚举2"),
        ENUM_3(3, "枚举3");

        private int value;      //值
        private String text;    //文本

        public int getValue() { return value; }

        public String getText() { return text; }

        TestDataEnum(int value, String text) {
            this.value = value;
            this.text = text;
        }

        /**
         * 根据值取枚举，找不到返回 null
         */
        public static TestDataEnum getByValue(int value) {
            for (TestDataEnum item : TestDataEnum.values()) {
                if (item.value == value) {
                    return item;
                }
            }
            return null;
        }
    }
}



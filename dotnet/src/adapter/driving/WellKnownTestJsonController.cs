using System.Dynamic;
using DddExample.Utility;
using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// 测试 JSON 数据输出 Well Known Controller
    /// </summary>
    public class WellKnownTestJsonController : ControllerBase
    {
        /// <summary>
        /// 测试 JSON 数据输出，类
        /// </summary>
        [HttpGet("/.well-known/test/json/class")]
        public Result TestJsonClass()
        {
            TestDataClass c = new TestDataClass();
            return Result.OkData(c);
        }

        /// <summary>
        /// 测试 JSON 数据输出，嵌套类
        /// </summary>
        [HttpGet("/.well-known/test/json/class-nested")]
        public Result TestJsonClassNested()
        {
            TestDataNestedClass c = new TestDataNestedClass();
            return Result.OkData(c);
        }

        /// <summary>
        /// 测试 JSON 数据输出，类ToDynamic
        /// </summary>
        [HttpGet("/.well-known/test/json/class/dynamic")]
        public Result TestJsonClassDynamic()
        {
            TestDataClass c = new TestDataClass();
            dynamic d = DynamicUtility.ToDynamic(c);
            return Result.OkData(d);
        }

        /// <summary>
        /// 测试 JSON 数据输出，嵌套类ToDynamic
        /// </summary>
        [HttpGet("/.well-known/test/json/class-nested/dynamic")]
        public Result TestJsonClassNestedDynamic()
        {
            TestDataNestedClass c = new TestDataNestedClass();
            dynamic d = DynamicUtility.ToDynamic(c);
            return Result.OkData(d);
        }

        /// <summary>
        /// 测试 JSON 数据输出，dynamic
        /// </summary>
        [HttpGet("/.well-known/test/json/dynamic")]
        public Result TestJsonDynamic()
        {
            dynamic d = new ExpandoObject();
            d.cls = new TestDataClass();
            d.list = new List<TestDataClass> { new TestDataClass() };
            IDictionary<string, TestDataClass> dict = new Dictionary<string, TestDataClass>();
            dict.Add("dictKey", new TestDataClass());
            d.dict = dict;
            return Result.OkData(d);
        }

        /// <summary>
        /// 构造dynamic集合转json输出
        /// </summary>
        [HttpGet("/.well-known/test/json/dynamic-list")]
        public Result TestJsonDynamicList()
        {
            List<dynamic> dl = new List<dynamic>();
            for (int i = 1; i <= 12; i++)
            {
                dynamic d = new ExpandoObject();
                d.cls = new TestDataClass();
                d.list = new List<TestDataClass> { new TestDataClass() };
                IDictionary<string, TestDataClass> dict = new Dictionary<string, TestDataClass>();
                dict.Add("dictKey", new TestDataClass());
                d.dict = dict;
                dl.Add(d);
            }
            return Result.OkData(dl);
        }
    }

    /// <summary>
    /// 用于测试 JSON 转换的数据类
    /// </summary>
    public class TestDataClass
    {
        public object? ObjectNull { get; set; } = null;
        public object ObjectNew { get; set; } = new object();

        public string? StringNull { get; set; } = null;
        public string StringEmpty { get; set; } = string.Empty;
        public string StringTest { get; set; } = "test";

        public bool BoolTrue { get; set; } = true;
        public bool BoolFalse { get; set; } = false;
        //public Boolean BooleanTrue { get; set; }  // Boolean 与 bool 等价
        //public Boolean BooleanFalse { get; set; }
        public bool? BoolNullableNull { get; set; } = null;
        public bool? BoolNullableTrue { get; set; } = true;
        public bool? BoolNullableFalse { get; set; } = false;

        public int IntZero { get; set; } = 0;
        public int IntMin { get; set; } = int.MinValue;
        public int IntMax { get; set; } = int.MaxValue;
        public int? IntNullableNull { get; set; } = null;
        public int? IntNullableZero { get; set; } = 0;
        public int? IntNullableMin { get; set; } = int.MinValue;
        public int? IntNullableMax { get; set; } = int.MaxValue;

        public long LongZero { get; set; } = 0L;
        public long LongMin { get; set; } = long.MinValue;
        public long LongMax { get; set; } = long.MaxValue;
        public long? LongNullableNull { get; set; } = null;
        public long? LongNullableZero { get; set; } = 0L;
        public long? LongNullableMin { get; set; } = long.MinValue;
        public long? LongNullableMax { get; set; } = long.MaxValue;

        public decimal DecimalZero { get; set; } = decimal.Zero;
        public decimal DecimalMin { get; set; } = decimal.MinValue;
        public decimal DecimalMax { get; set; } = decimal.MaxValue;
        public decimal? DecimalNullableNull { get; set; } = null;
        public decimal? DecimalNullableZero { get; set; } = decimal.Zero;
        public decimal? DecimalNullableMin { get; set; } = decimal.MinValue;
        public decimal? DecimalNullableMax { get; set; } = decimal.MaxValue;

        public DateTime DateTimeNow { get; set; } = DateTime.Now;
        public DateTime DateTimeMin { get; set; } = DateTime.MinValue;
        public DateTime DateTimeMax { get; set; } = DateTime.MaxValue;
        public DateTime? DateTimeNullableNull { get; set; } = null;
        public DateTime? DateTimeNullableNow { get; set; } = DateTime.Now;
        public DateTime? DateTimeNullableMin { get; set; } = DateTime.MinValue;
        public DateTime? DateTimeNullableMax { get; set; } = DateTime.MaxValue;

        public DateOnly DateOnlyNow { get; set; } = DateOnly.FromDateTime(DateTime.Now);
        public DateOnly DateOnlyMin { get; set; } = DateOnly.MinValue;
        public DateOnly DateOnlyMax { get; set; } = DateOnly.MaxValue;
        public DateOnly? DateOnlyNullableNull { get; set; } = null;
        public DateOnly? DateOnlyNullableNow { get; set; } = DateOnly.FromDateTime(DateTime.Now);
        public DateOnly? DateOnlyNullableMin { get; set; } = DateOnly.MinValue;
        public DateOnly? DateOnlyNullableMax { get; set; } = DateOnly.MaxValue;

        public TimeOnly TimeOnlyNow { get; set; } = TimeOnly.FromDateTime(DateTime.Now);
        public TimeOnly TimeOnlyMin { get; set; } = TimeOnly.MinValue;
        public TimeOnly TimeOnlyMax { get; set; } = TimeOnly.MaxValue;
        public TimeOnly? TimeOnlyNullableNull { get; set; } = null;
        public TimeOnly? TimeOnlyNullableNow { get; set; } = TimeOnly.FromDateTime(DateTime.Now);
        public TimeOnly? TimeOnlyNullableMin { get; set; } = TimeOnly.MinValue;
        public TimeOnly? TimeOnlyNullableMax { get; set; } = TimeOnly.MaxValue;

        public TestDataEnum Enum1 { get; set; } = TestDataEnum.ENUM_1;
        public TestDataEnum Enum2 { get; set; } = TestDataEnum.ENUM_2;
        public TestDataEnum? EnumNullableNull { get; set; } = null;
        public TestDataEnum? EnumNullableEnum1 { get; set; } = TestDataEnum.ENUM_1;
        public TestDataEnum? EnumNullableEnum2 { get; set; } = TestDataEnum.ENUM_2;
    }

    /// <summary>
    /// 用于测试 JSON 转换的数据类
    /// </summary>
    public class TestDataNestedClass
    {
        public TestDataClass Cls { get; set; }

        public IList<TestDataClass> List { get; set; }

        public IDictionary<string, TestDataClass> Dictionary { get; set; }

        public TestDataNestedClass()
        {
            Cls = new TestDataClass();
            List = new List<TestDataClass>();
            List.Add(new TestDataClass());
            Dictionary = new Dictionary<string, TestDataClass>();
            Dictionary.Add("dictKey", new TestDataClass());
        }
    }

    /// <summary>
    /// 用于测试 JSON 转换的枚举
    /// </summary>
    public enum TestDataEnum
    {
        ENUM_1 = 1,
        ENUM_2 = 2,
        ENUM_3 = 3,
    }
}

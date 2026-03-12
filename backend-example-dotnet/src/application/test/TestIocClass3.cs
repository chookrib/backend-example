namespace BackendExample.Application.Test
{
    /// <summary>
    /// IoC测试类3
    /// </summary>
    public class TestIocClass3
    {
        /// <summary>
        /// 不会引起循环依赖的构造函数
        /// </summary>
        public TestIocClass3()
        {
        }

        //private readonly TestIocClass1 class1;

        ///// <summary>
        ///// 会引起循环依赖的构造函数
        ///// </summary>
        //public TestIocClass3(TestIocClass1 class1)
        //{
        //    this.class1 = class1;
        //}
    }
}

namespace BackendExample.Application.Test
{
    /// <summary>
    /// IoC测试类1
    /// </summary>
    public class TestIocClass1
    {
        private readonly TestIocClass2 class2;

        public TestIocClass1(TestIocClass2 class2)
        {
            this.class2 = class2;
        }
    }
}

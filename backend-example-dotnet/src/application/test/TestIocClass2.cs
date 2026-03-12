namespace BackendExample.Application.Test
{
    /// <summary>
    /// IoC测试类2
    /// </summary>
    public class TestIocClass2
    {
        private readonly TestIocClass3 clss3;

        public TestIocClass2(TestIocClass3 clss3)
        {
            this.clss3 = clss3;
        }
    }
}

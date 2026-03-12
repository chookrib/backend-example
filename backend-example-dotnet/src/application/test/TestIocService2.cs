namespace BackendExample.Application.Test
{
    /// <summary>
    /// 循环依赖测试类2
    /// </summary>
    public class TestIocService2
    {
        private readonly TestIocService1 service1;

        public TestIocService2(TestIocService1 service1)
        {
            this.service1 = service1;
        }
    }
}

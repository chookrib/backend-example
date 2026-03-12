namespace BackendExample.Application.Test
{
    /// <summary>
    /// 循环依赖测试类1
    /// </summary>
    public class TestIocService1
    {
        private readonly TestIocService2 service2;

        public TestIocService1(TestIocService2 service2)
        {
            this.service2 = service2;
        }
    }
}

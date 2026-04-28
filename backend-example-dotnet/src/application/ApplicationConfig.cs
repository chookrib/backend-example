namespace BackendExample.Application
{
    /// <summary>
    /// 应用配置
    /// </summary>
    public class ApplicationConfig
    {
        private readonly string appName;
        private readonly string appEnv;
        //private readonly IServiceProvider serviceProvider;

        public ApplicationConfig(
            IConfiguration configuration
            // , IServiceProvider serviceProvider
            )
        {
            this.appName = configuration.GetValue<string>("App:Name", string.Empty);
            this.appEnv = configuration.GetValue<string>("App:Env", string.Empty);
            //this.serviceProvider = serviceProvider;
        }

        /// <summary>
        /// 应用名称
        /// </summary>
        public string GetAppName()
        {
            return this.appName;
        }

        /// <summary>
        /// 应用运行环境
        /// </summary>
        public string GetAppEnv()
        {
            return this.appEnv;
        }

        /// <summary>
        /// 应用运行环境是否是开发环境
        /// </summary>
        /// <returns></returns>
        public bool IsAppEnvDev()
        {
            return this.appEnv.ToLower() == "dev";
            //configuration.GetValue<string>("ASPNETCORE_ENVIRONMENT") == "Development";
            //Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT") == "Development";
        }

        ///// <summary>
        ///// 从容器中获取实例
        ///// </summary>
        //public T GetService<T>()
        //{
        //    if (this.serviceProvider == null)
        //        throw new Exception($"没有提供 IServiceProvider");
        //    T? t = this.serviceProvider.GetService<T>();
        //    if (t == null)
        //        throw new Exception($"IServiceProvider 中没有注册 {typeof(T)}");
        //    return t;
        //}
    }
}

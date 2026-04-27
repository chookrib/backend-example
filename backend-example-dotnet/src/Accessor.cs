namespace BackendExample;

/// <summary>
/// 访问器，方便在应用程序中访问配置和服务
/// </summary>
public class Accessor
{
    /// <summary>
    /// 应用名称
    /// </summary>
    public static string AppName { get; set; } = string.Empty;

    /// <summary>
    /// 应用运行环境
    /// </summary>
    public static string AppEnv { get; set; } = string.Empty;

    /// <summary>
    /// 应用运行环境是否为开发环境
    /// </summary>
    public static bool AppEnvIsDev { get{ return AppEnv.ToLower() == "dev"; } }

    /// <summary>
    /// 配置
    /// </summary>
    public static IConfiguration? Configuration { get; set; }

    /// <summary>
    /// 是否是开发环境
    /// </summary>

    /// <summary>
    /// 容器
    /// </summary>
    public static IServiceProvider? ServiceProvider { get; set; }

    /// <summary>
    /// 从容器中获取实例
    /// </summary>
    public static T GetService<T>()
    {
        if (ServiceProvider == null)
            throw new Exception($"没有为 Accessor 提供 ServiceProvider");
        T? t = ServiceProvider.GetService<T>();
        if (t == null)
            throw new Exception($"Accessor ServiceProvider 中没有注册 {typeof(T)}");
        return t;
    }
}
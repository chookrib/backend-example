namespace BackendExample;

/// <summary>
/// 访问器
/// </summary>
public class Accessor
{
    /// <summary>
    /// 应用名称
    /// </summary>
    public static string AppName { get; set; } = string.Empty;

    /// <summary>
    /// 应用是否为开发环境
    /// </summary>
    public static bool AppEnvIsDev { get; set; }

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
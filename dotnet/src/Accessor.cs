namespace DddExample;

/// <summary>
/// 访问器
/// </summary>
public class Accessor
{
    /// <summary>
    /// 容器
    /// </summary>
    public static IServiceProvider ServiceProvider { get; set; }

    /// <summary>
    /// 配置
    /// </summary>
    public static IConfiguration Configuration { get; set; }
}
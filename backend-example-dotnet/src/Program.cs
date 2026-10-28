using log4net;
using log4net.Config;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc.Filters;
using src.adapter.driven;
using src.adapter.driven.ext;
using src.adapter.driven.user;
using src.adapter.driving;
using src.application;
using src.application.@lock;
using src.application.test;
using src.application.user;
using src.domain;
using src.domain.ext;
using src.domain.user;
using src.utility;
using System.Globalization;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace src
{
    public class Program
    {
        private static readonly ILog logger = LogManager.GetLogger(typeof(Program));

        /// <summary>
        /// 打印所有配置
        /// </summary>
        private static void PrintConfiguration(IConfiguration config, string parentKey = "")
        {
            foreach (var child in config.GetChildren())
            {
                string key = string.IsNullOrEmpty(parentKey) ? child.Key : $"{parentKey}:{child.Key}";
                if (child.GetChildren().Any())
                    PrintConfiguration(child, key);
                else
                    Console.WriteLine($"    {key} = {child.Value}");
            }
        }

        public static void Main(string[] args)
        {
            // 配置log4net
            XmlConfigurator.Configure(new FileInfo("log4net.config"));
            LogManager.GetLogger(typeof(Program)).Info("log4net 配置成功");

            WebApplicationBuilder builder = WebApplication.CreateBuilder(args);

            // 跨域配置
            // builder.Services.AddCors(options =>
            // {
            //     options.AddDefaultPolicy(policy =>
            //         policy.AllowAnyOrigin()      // 允许所有来源
            //               .AllowAnyMethod()      // 允许所有方法
            //               .AllowAnyHeader());    // 允许所有头
            // });

            // =========================================================================================================
            // 注册 ApplicationConfig
            builder.Services.AddSingleton<ApplicationConfig>();

            // 注册锁 Service
            string lockService = builder.Configuration.GetValue<string>("App:LockService", "semaphore").ToLower();
            if (lockService == "semaphore")
                builder.Services.AddSingleton<LockService, SemaphoreLockService>();
            else if (lockService == "redis")
                builder.Services.AddSingleton<LockService, RedisLockService>();
            else
                throw new Exception("App:LockService 配置错误");
            builder.Services.AddSingleton<TestLockService>();

            // 注册 Driven Adapter - Gateway
            builder.Services.AddSingleton<SmsGateway, SmsGatewayAdapter>();
            // 注册 Driven Adapter - Persistence
            builder.Services.AddSingleton<DatabaseAdapter>();
            builder.Services.AddSingleton<UserPersistenceAdapter>();
            builder.Services.AddSingleton<UserRepository>(sp => sp.GetRequiredService<UserPersistenceAdapter>());
            builder.Services.AddSingleton<UserUniqueSpecification>(sp => sp.GetRequiredService<UserPersistenceAdapter>());
            builder.Services.AddSingleton<UserQueryHandler>(sp => sp.GetRequiredService<UserPersistenceAdapter>());
            // 注册 Application Service
            builder.Services.AddSingleton<UserAuthService>();
            builder.Services.AddSingleton<UserProfileService>();
            builder.Services.AddSingleton<UserManageService>();

            // 测试IoC注册，循环依赖会报错
            builder.Services.AddSingleton<TestIocClass3>();
            builder.Services.AddSingleton<TestIocClass2>();
            builder.Services.AddSingleton<TestIocClass1>();

            // 注册 Controller
            builder.Services.AddControllers()
                // .AddMvcOptions(options => options.Filters.Add<AccessTokenFilter>())     // 拦截器
                .AddJsonOptions(options =>
                {
                    options.JsonSerializerOptions.Converters.Add(new LongToStringConverter());
                    options.JsonSerializerOptions.Converters.Add(new LongNullableToStringConverter());
                    options.JsonSerializerOptions.Converters.Add(new DecimalToStringConverter());
                    options.JsonSerializerOptions.Converters.Add(new DecimalNullableToStringConverter());
                    options.JsonSerializerOptions.Converters.Add(new DateTimeConverter("yyyy-MM-dd HH:mm:ss"));
                    options.JsonSerializerOptions.Converters.Add(new DateTimeNullableConverter("yyyy-MM-dd HH:mm:ss"));
                    options.JsonSerializerOptions.Converters.Add(new DateOnlyConverter("yyyy-MM-dd"));
                    options.JsonSerializerOptions.Converters.Add(new DateOnlyNullableConverter("yyyy-MM-dd"));
                    options.JsonSerializerOptions.Converters.Add(new TimeOnlyConverter("HH:mm:ss"));
                    options.JsonSerializerOptions.Converters.Add(new TimeOnlyNullableConverter("HH:mm:ss"));

                    // options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());                 // 枚举转换为字符串

                    // options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull;  // 忽略 null 值
                    // options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;             // 属性命名策略
                    // options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;              // 忽略循环引用
                });

            // 允许同步 IO 可能会带来性能问题，不推荐在生产环境使用
            // Kestrel 允许同步 IO 
            // builder.WebHost.ConfigureKestrel(serverOptions => { serverOptions.AllowSynchronousIO = true; });
            // IIS 允许同步 IO
            // builder.Services.Configure<IISServerOptions>(options => { options.AllowSynchronousIO = true; });

            //==========================================================================================================

            WebApplication app = builder.Build();

            // 启用跨域，UseAuthorization 前，MapControllers 前
            // app.UseCors();

            // Configure the HTTP request pipeline.
            // app.UseAuthorization();

            ApplicationConfig applicationConfig = app.Services.GetRequiredService<ApplicationConfig>();

            // 全局异常处理
            app.UseExceptionHandler(configure =>
            {
                configure.Run(async context =>
                {
                    context.Response.StatusCode = StatusCodes.Status200OK;
                    context.Response.ContentType = "application/json";
                    Exception? error = context.Features.Get<IExceptionHandlerFeature>()?.Error;
                    if (error is NotLoginException)
                    {
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_NOT_LOGIN, "未登录")
                        );
                    }
                    else if (error is LockException)
                    {
                        logger.Error("捕捉到 LockException 异常: ", error);
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_DEFAULT, "系统繁忙，请稍后再试")
                        );
                    }
                    else if (error is UtilityException
                        || error is DomainException
                        || error is src.application.ApplicationException
                        || error is PersistenceException
                        || error is GatewayException
                        || error is ControllerException)
                    {
                        // 自定义异常返回异常信息
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_DEFAULT, error?.Message ?? string.Empty)
                        );
                    }
                    else
                    {
                        // 非自定义异常记录日志
                        logger.Error("捕捉到非自定义异常", error);

                        if (applicationConfig.IsAppEnvDev())
                        {
                            // 开发环境非自定义异常显示具体信息
                            await context.Response.WriteAsJsonAsync(
                                Result.Error(ResultCodes.ERROR_DEFAULT, error?.Message ?? string.Empty)
                            );
                        }
                        else
                        {
                            // 生产环境非自定义异常显示统一出错信息
                            await context.Response.WriteAsJsonAsync(
                                Result.Error(ResultCodes.ERROR_DEFAULT, "系统发生错误")
                            );
                        }
                    }
                });
            });

            app.MapControllers();

            //==========================================================================================================

            // 仅在开发环境打印配置，不记录日志
            if (applicationConfig.IsAppEnvDev())
            {
                Console.WriteLine($"Configuration:");
                PrintConfiguration(builder.Configuration);
            }

            if (ValueUtility.IsEmptyString(applicationConfig.GetAppName()))
                logger.Warn("App:Name 配置缺失");

            logger.Info($"应用启动完成: {applicationConfig.GetAppName()}");

            //==========================================================================================================

            app.Run();
        }

        #region json 转换器
        private class LongToStringConverter : JsonConverter<long>
        {
            public override long Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => long.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, long value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        private class LongNullableToStringConverter : JsonConverter<long?>
        {
            public override long? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToLongOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, long? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        private class DecimalToStringConverter : JsonConverter<decimal>
        {
            public override decimal Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => decimal.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, decimal value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(CultureInfo.InvariantCulture));
        }

        private class DecimalNullableToStringConverter : JsonConverter<decimal?>
        {
            public override decimal? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDecimalOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, decimal? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        private class DateTimeConverter : JsonConverter<DateTime>
        {
            private readonly string format;

            public DateTimeConverter(string format = "yyyy-MM-dd HH:mm:ss") => this.format = format;

            public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => DateTime.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(format));
        }

        private class DateTimeNullableConverter : JsonConverter<DateTime?>
        {
            private readonly string format;

            public DateTimeNullableConverter(string format = "yyyy-MM-dd HH:mm:ss") => this.format = format;

            public override DateTime? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateTime? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(format) ?? string.Empty);
        }

        private class DateOnlyConverter : JsonConverter<DateOnly>
        {
            private readonly string format;

            public DateOnlyConverter(string format = "yyyy-MM-dd") => this.format = format;

            public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => DateOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(format));
        }

        private class DateOnlyNullableConverter : JsonConverter<DateOnly?>
        {
            private readonly string format;

            public DateOnlyNullableConverter(string format = "yyyy-MM-dd") => this.format = format;

            public override DateOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(format) ?? string.Empty);
        }

        private class TimeOnlyConverter : JsonConverter<TimeOnly>
        {
            private readonly string format;

            public TimeOnlyConverter(string format = "HH:mm:ss") => this.format = format;

            public override TimeOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => TimeOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(format));
        }

        private class TimeOnlyNullableConverter : JsonConverter<TimeOnly?>
        {
            private readonly string format;

            public TimeOnlyNullableConverter(string format = "HH:mm:ss") => this.format = format;

            public override TimeOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(format) ?? string.Empty);
        }

        #endregion

        #region 拦截器
        // public class AccessTokenFilter : IAsyncActionFilter
        // {
        //     private readonly UserAuthService userAuthService;

        //     public AccessTokenFilter(UserAuthService userAuthService)
        //         => this.userAuthService = userAuthService;

        //     public async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
        //     {
        //         HttpContext http = context.HttpContext;
        //         PathString path = http.Request.Path;

        //         if (!path.StartsWithSegments("/api/"))
        //         {
        //             await next();
        //             return;
        //         }

        //         if (path.StartsWithSegments("/api/user/login") || path.StartsWithSegments("/api/user/register"))
        //         {
        //             await next();
        //             return;
        //         }

        //         // var userAuthService = http.RequestServices.GetRequiredService<UserAuthService>();
        //         string token = http.Request.Headers["Access-Token"].FirstOrDefault() ?? string.Empty;
        //         string userId = userAuthService.GetLoginUserId(token);
        //         if (ValueUtility.IsEmptyString(userId))
        //         {
        //             throw new NotLoginException();
        //         }

        //         await next();
        //     }
        // }
        #endregion
    }
}
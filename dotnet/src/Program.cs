using System.Globalization;
using System.Text.Json;
using System.Text.Json.Serialization;

using BackendExample.Adapter.Driven;
using BackendExample.Adapter.Driving;
using BackendExample.Application;
using BackendExample.Domain;
using BackendExample.Utility;

using log4net;
using log4net.Config;

using Microsoft.AspNetCore.Diagnostics;

namespace BackendExample
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
                    Console.WriteLine($"{key} = {child.Value}");
            }
        }

        public static void Main(string[] args)
        {
            // 配置log4net
            XmlConfigurator.Configure(new FileInfo("log4net.config"));
            LogManager.GetLogger(typeof(Program)).Info("log4net 配置成功");

            WebApplicationBuilder builder = WebApplication.CreateBuilder(args);
            PrintConfiguration(builder.Configuration);

            // 将 配置 存储到 Accessor 中，方便在非 DI 环境中使用
            Accessor.Configuration = builder.Configuration;
            Accessor.IsDevelopment = builder.Configuration.GetValue<string>("ASPNETCORE_ENVIRONMENT") == "Development";
            // Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT") == "Development";

            // =========================================================================================================

            // 注册服务
            builder.Services.AddSingleton<SmsGateway, SmsGatewayAdapter>();

            builder.Services.AddSingleton<UserPersistenceAdapter>();
            builder.Services.AddSingleton<UserRepository>(sp => sp.GetRequiredService<UserPersistenceAdapter>());
            builder.Services.AddSingleton<UserUniqueSpecification>(sp => sp.GetRequiredService<UserPersistenceAdapter>());
            builder.Services.AddSingleton<UserQueryHandler>(sp => sp.GetRequiredService<UserPersistenceAdapter>());

            builder.Services.AddSingleton<LockService, SemaphoreLockService>();
            builder.Services.AddSingleton<TestLockService>();

            builder.Services.AddSingleton<UserAuthService>();
            builder.Services.AddSingleton<UserProfileService>();
            builder.Services.AddSingleton<UserManageService>();

            // 注册 Controller
            builder.Services.AddControllers().AddJsonOptions(options =>
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

                //options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());  // 枚举转换为字符串

                //options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull; // 忽略 null 值
                //options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;            // 属性命名策略
                //options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;             // 忽略循环引用
            });

            // 允许同步 IO 可能会带来性能问题，不推荐在生产环境使用
            // Kestrel 允许同步 IO 
            // builder.WebHost.ConfigureKestrel(serverOptions => { serverOptions.AllowSynchronousIO = true; });
            // IIS 允许同步 IO
            // builder.Services.Configure<IISServerOptions>(options => { options.AllowSynchronousIO = true; });

            //==========================================================================================================

            WebApplication app = builder.Build();

            // 将 容器 存储到 Accessor 中，方便在非 DI 环境中使用
            Accessor.ServiceProvider = app.Services;

            // Configure the HTTP request pipeline.

            //app.UseAuthorization();

            // 全局异常处理
            app.UseExceptionHandler(configure =>
            {
                configure.Run(async context =>
                {
                    context.Response.StatusCode = StatusCodes.Status200OK;
                    context.Response.ContentType = "application/json";
                    var error = context.Features.Get<IExceptionHandlerFeature>()?.Error;
                    if (error is NotLoginException)
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_NOT_LOGIN, "未登录")
                        );
                    else
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_DEFAULT, error?.Message ?? "未知异常")
                        );
                });
            });

            app.MapControllers();

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
                => writer.WriteStringValue(value.ToString(this.format));
        }

        private class DateTimeNullableConverter : JsonConverter<DateTime?>
        {
            private readonly string format;

            public DateTimeNullableConverter(string format = "yyyy-MM-dd HH:mm:ss") => this.format = format;

            public override DateTime? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateTime? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(this.format) ?? string.Empty);
        }

        private class DateOnlyConverter : JsonConverter<DateOnly>
        {
            private readonly string format;

            public DateOnlyConverter(string format = "yyyy-MM-dd") => this.format = format;

            public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => DateOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(this.format));
        }

        private class DateOnlyNullableConverter : JsonConverter<DateOnly?>
        {
            private readonly string format;

            public DateOnlyNullableConverter(string format = "yyyy-MM-dd") => this.format = format;

            public override DateOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(this.format) ?? string.Empty);
        }

        private class TimeOnlyConverter : JsonConverter<TimeOnly>
        {
            private readonly string format;

            public TimeOnlyConverter(string format = "HH:mm:ss") => this.format = format;

            public override TimeOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => TimeOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(this.format));
        }

        private class TimeOnlyNullableConverter : JsonConverter<TimeOnly?>
        {
            private readonly string format;

            public TimeOnlyNullableConverter(string format = "HH:mm:ss") => this.format = format;

            public override TimeOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(this.format) ?? string.Empty);
        }

        #endregion
    }
}
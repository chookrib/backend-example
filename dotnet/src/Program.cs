using System.Text.Json;
using System.Text.Json.Serialization;
using DddExample.Adapter.Driven;
using DddExample.Adapter.Driving;
using DddExample.Application;
using DddExample.Domain;
using DddExample.Utility;

using Microsoft.AspNetCore.Diagnostics;

namespace DddExample
{
    public class Program
    {
        public static void Main(string[] args)
        {
            WebApplicationBuilder builder = WebApplication.CreateBuilder(args);

            // ×¢²á·þÎñ
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

            builder.Services.AddSingleton<RequestAuthHelper>();

            // ×¢²á Controller
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

                //options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());  // Ã¶¾Ù×ª»»Îª×Ö·û´®

                //options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull; // ºöÂÔ null Öµ
                //options.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;            // ÊôÐÔÃüÃû²ßÂÔ
                //options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;             // ºöÂÔÑ­»·ÒýÓÃ
            });

            //==========================================================================================================

            WebApplication app = builder.Build();

            // Configure the HTTP request pipeline.

            //app.UseAuthorization();

            app.UseExceptionHandler(configure =>
            {
                configure.Run(async context =>
                {
                    context.Response.StatusCode = StatusCodes.Status200OK;
                    context.Response.ContentType = "application/json";
                    var error = context.Features.Get<IExceptionHandlerFeature>()?.Error;
                    if (error is NotLoginException)
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_NOT_LOGIN, "Î´µÇÂ¼")
                        );
                    else
                        await context.Response.WriteAsJsonAsync(
                            Result.Error(ResultCodes.ERROR_DEFAULT, error?.Message ?? "Î´ÖªÒì³£")
                        );
                });
            });

            app.MapControllers();

            app.Run();
        }

        #region json ×ª»»Æ÷
        public class LongToStringConverter : JsonConverter<long>
        {
            public override long Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => long.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, long value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        public class LongNullableToStringConverter : JsonConverter<long?>
        {
            public override long? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToLongOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, long? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        public class DecimalToStringConverter : JsonConverter<decimal>
        {
            public override decimal Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => decimal.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, decimal value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        public class DecimalNullableToStringConverter : JsonConverter<decimal?>
        {
            public override decimal? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDecimalOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, decimal? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString());
        }

        public class DateTimeConverter : JsonConverter<DateTime>
        {
            private readonly string _format;
            public DateTimeConverter(string format = "yyyy-MM-dd HH:mm:ss") => _format = format;

            public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => DateTime.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(_format));
        }

        public class DateTimeNullableConverter : JsonConverter<DateTime?>
        {
            private readonly string _format;
            public DateTimeNullableConverter(string format = "yyyy-MM-dd HH:mm:ss") => _format = format;

            public override DateTime? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateTime? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(_format) ?? string.Empty);
        }

        public class DateOnlyConverter : JsonConverter<DateOnly>
        {
            private readonly string _format;
            public DateOnlyConverter(string format = "yyyy-MM-dd") => _format = format;

            public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => DateOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(_format));
        }

        public class DateOnlyNullableConverter : JsonConverter<DateOnly?>
        {
            private readonly string _format;
            public DateOnlyNullableConverter(string format = "yyyy-MM-dd") => _format = format;

            public override DateOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToDateOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, DateOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(_format) ?? string.Empty);
        }

        public class TimeOnlyConverter : JsonConverter<TimeOnly>
        {
            private readonly string _format;
            public TimeOnlyConverter(string format = "HH:mm:ss") => _format = format;

            public override TimeOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => TimeOnly.Parse(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly value, JsonSerializerOptions options)
                => writer.WriteStringValue(value.ToString(_format));
        }

        public class TimeOnlyNullableConverter : JsonConverter<TimeOnly?>
        {
            private readonly string _format;
            public TimeOnlyNullableConverter(string format = "HH:mm:ss") => _format = format;

            public override TimeOnly? Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
                => ValueUtility.ToTimeOrNull(reader.GetString()!);

            public override void Write(Utf8JsonWriter writer, TimeOnly? value, JsonSerializerOptions options)
                => writer.WriteStringValue(value?.ToString(_format) ?? string.Empty);
        }
        #endregion
    }
}

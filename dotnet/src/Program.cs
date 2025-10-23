using DddExample.Application;

using Microsoft.Extensions.DependencyInjection.Extensions;

namespace DddExample
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // ×¢²á·þÎñ
            builder.Services.AddSingleton<LockService, SemaphoreLockService>();
            builder.Services.AddSingleton<TestLockService>();

            builder.Services.AddControllers();
            
            var app = builder.Build();

            // Configure the HTTP request pipeline.

            //app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}

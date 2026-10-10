using System.Reflection;
using System.Text;
using BackendExample.Utility;
using Microsoft.AspNetCore.Mvc;

namespace BackendExample.Adapter.Driving
{
    /// <summary>
    /// Well Known Controller
    /// </summary>
    public class WellKnownController : ControllerBase
    {
        private static readonly DateTime startTime = DateTime.Now;

        /// <summary>
        /// 应用信息，显示非涉密信息
        /// </summary>
        [HttpGet("/.well-known/metadata")]
        [Produces("text/plain")]
        public string Metadata()
        {
            Assembly assembly = Assembly.GetExecutingAssembly();
            IEnumerable<AssemblyMetadataAttribute> metadataAttrs = assembly.GetCustomAttributes<AssemblyMetadataAttribute>();
            AssemblyMetadataAttribute? buildTimeAttr = metadataAttrs.FirstOrDefault(a => a.Key == "BuildTime");

            StringBuilder sb = new StringBuilder();
            //foreach (AssemblyMetadataAttribute attr in metadataAttrs)
            //    sb.AppendLine($"AssemblyMetadataAttribute {attr.Key}: {attr.Value}");
            sb.AppendLine($"Build-Time: {buildTimeAttr?.Value}");

            AssemblyInformationalVersionAttribute? versionAttr = assembly.GetCustomAttribute<AssemblyInformationalVersionAttribute>();
            sb.AppendLine($"Informational-Version: {versionAttr?.InformationalVersion}");

            sb.AppendLine($"Start-Time: {ValueUtility.FormatDateTime(startTime)}");

            return sb.ToString();
        }

        /// <summary>
        /// 应用信息，显示非涉密信息，JSON格式
        /// </summary>
        [HttpGet("/.well-known/metadata.json")]
        public IActionResult MetadataJson()
        {
            Assembly assembly = Assembly.GetExecutingAssembly();
            var buildTimeAttr = assembly.GetCustomAttributes<AssemblyMetadataAttribute>().FirstOrDefault(a => a.Key == "BuildTime");
            var versionAttr = assembly.GetCustomAttribute<AssemblyInformationalVersionAttribute>();

            var response = new
            {
                BuildTime = buildTimeAttr?.Value,
                InformationalVersion = versionAttr?.InformationalVersion,
                StartTime = ValueUtility.FormatDateTime(startTime)
            };

            return Ok(response);
        }
    }
}

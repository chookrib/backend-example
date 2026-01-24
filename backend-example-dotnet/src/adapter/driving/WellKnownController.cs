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

        [HttpGet("/api/.well-known")]
        [Produces("text/plain")]
        public string WellKnown()
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
    }
}

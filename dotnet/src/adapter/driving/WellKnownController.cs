using System.Reflection;
using System.Text;
using Microsoft.AspNetCore.Mvc;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// Well Known Controller
    /// </summary>
    public class WellKnownController : ControllerBase
    {
        [HttpGet("/.well-known/info")]
        [Produces("text/plain")]
        public string Info()
        {
            Assembly assembly = Assembly.GetExecutingAssembly();
            IEnumerable<AssemblyMetadataAttribute> metadataAttrs = assembly.GetCustomAttributes<AssemblyMetadataAttribute>();
            AssemblyMetadataAttribute? buildTimeAttr = metadataAttrs.FirstOrDefault(a => a.Key == "BuildTime");

            StringBuilder sb = new StringBuilder();
            //foreach (AssemblyMetadataAttribute attr in metadataAttrs)
            //    sb.AppendLine($"AssemblyMetadataAttribute {attr.Key}: {attr.Value}");
            sb.AppendLine($"AssemblyMetadata BuildTime: {buildTimeAttr?.Value}");

            AssemblyInformationalVersionAttribute? versionAttr = assembly.GetCustomAttribute<AssemblyInformationalVersionAttribute>();
            sb.AppendLine($"AssemblyInformationalVersionAttribute: {versionAttr?.InformationalVersion}");

            return sb.ToString();
        }
    }
}

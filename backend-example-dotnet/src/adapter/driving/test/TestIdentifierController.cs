using Microsoft.AspNetCore.Mvc;
using src.utility;

namespace src.adapter.driving.test
{
    /// <summary>
    /// Identifier 测试 Controller
    /// </summary>
    public class TestIdentifierController : ControllerBase
    {
        [HttpGet("/api/test/id")]
        public Result TestId()
        {
            return Result.OkData(new
            {
                join = new
                {
                    e0 = IdentifierUtility.WrapJoin(new List<string>()),
                    e1 = IdentifierUtility.WrapJoin(new List<string>() { "" }),
                    e2 = IdentifierUtility.WrapJoin(new List<string>() { "", "" }),
                    e3 = IdentifierUtility.WrapJoin(new List<string>() { "", "", "" }),
                    v1 = IdentifierUtility.WrapJoin(new List<string>() { "a" }),
                    v2 = IdentifierUtility.WrapJoin(new List<string>() { "a", "b" }),
                    v3 = IdentifierUtility.WrapJoin(new List<string>() { "a", "b", "c" })
                },
                split = new
                {
                    e0 = IdentifierUtility.Split(""),
                    e1 = IdentifierUtility.Split(",,"),
                    e2 = IdentifierUtility.Split(",,,"),
                    e3 = IdentifierUtility.Split(",,,,"),
                    v1 = IdentifierUtility.Split(",a,"),
                    v2 = IdentifierUtility.Split(",a,b,"),
                    v3 = IdentifierUtility.Split(",a,b,c,")
                }
            });
        }
    }
}

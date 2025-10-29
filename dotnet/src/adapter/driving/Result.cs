using System.Dynamic;
using DddExample.Utility;

namespace DddExample.Adapter.Driving
{
    /// <summary>
    /// Controller 返回结果
    /// </summary>
    public class Result
    {
        /// <summary>
        /// 返回码
        /// </summary>
        public int Code { get; private set; }

        /// <summary>
        /// 返回消息
        /// </summary>
        public string Message { get; private set; }

        /// <summary>
        /// 返回数据
        /// </summary>
        public object? Data { get; private set; }

        /// <summary>
        /// 构造函数
        /// </summary>
        private Result(int code, string message)
        {
            Code = code;
            Message = message;
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        private Result(int code, string message, object data)
        {
            Code = code;
            Message = message;
            Data = data;
        }

        /// <summary>
        /// 是否成功
        /// </summary>
        public bool Success
        {
            get { return Code == ResultCodes.SUCCESS; }
        }

        /// <summary>
        /// 成功
        /// </summary>
        public static Result Ok()
        {
            return new Result(ResultCodes.SUCCESS, string.Empty);
        }

        /// <summary>
        /// 成功，指定信息
        /// </summary>
        public static Result Ok(string message)
        {
            return new Result(ResultCodes.SUCCESS, message);
        }

        /// <summary>
        /// 成功，指定信息和数据
        /// </summary>
        public static Result Ok(string message, object data)
        {
            return new Result(ResultCodes.SUCCESS, message, data);
        }

        /// <summary>
        /// 成功，指定数据
        /// </summary>
        public static Result OkData(object data)
        {
            return new Result(ResultCodes.SUCCESS, string.Empty, data);
        }

        /// <summary>
        /// 失败，指定返回码
        /// </summary>
        public static Result Error(int code)
        {
            if (code == ResultCodes.SUCCESS)
                code = ResultCodes.ERROR_DEFAULT;
            return new Result(code, string.Empty);
        }

        /// <summary>
        /// 失败，指定返回码和信息
        /// </summary>
        public static Result Error(int code, string message)
        {
            if (code == ResultCodes.SUCCESS)
                code = ResultCodes.ERROR_DEFAULT;
            return new Result(code, message);
        }

        /// <summary>
        /// 失败，指定返回码、信息和数据
        /// </summary>
        public static Result Error(int code, string message, object data)
        {
            if (code == ResultCodes.SUCCESS)
                code = ResultCodes.ERROR_DEFAULT;
            return new Result(code, message, data);
        }

        /// <summary>
        /// 转换为dynamic，便于动态添加属性
        /// </summary>
        public dynamic GetDynamic()
        {
            dynamic d = new ExpandoObject();
            d.code = Code;
            d.meessage = Message;
            d.data = Data;
            d.success = Success;
            return d;
        }

        /// <summary>
        /// 解析 Result 中的 Data
        /// </summary>
        public static dynamic ParseData(string result)
        {
            int code;
            bool success;
            string message;
            dynamic data;
            try
            {
                dynamic json = JsonUtility.Deserialize(result);
                code = json.code;
                success = json.success;
                message = json.message;
                data = json.data;
            }
            catch (Exception ex)
            {
                throw new Exception("解析 Result 时发生异常: " + ex.Message);
            }

            if (success)
                return data;
            else
                throw new Exception(string.Format("解析 Result时发生错误: code={0} message={1}",
                    code, message));
        }
    }
}

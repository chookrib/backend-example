using System.Dynamic;
using System.Reflection;

namespace DddExample.Utility
{
    /// <summary>
    /// dynamic Utility
    /// </summary>
    public class DynamicUtility
    {
        /// <summary>
        /// 转换为 dynamic
        /// </summary>
        public static dynamic ToDynamic<T>(T obj)
        {
            //使用JsonConvert会丢失自定义 Attribute 信息
            //string json = JsonConvert.SerializeObject(obj);
            //return JsonConvert.DeserializeObject(json, typeof(ExpandoObject));

            IDictionary<string, object?> expando = new ExpandoObject();

            foreach (PropertyInfo propertyInfo in typeof(T).GetProperties())
            {
                expando.Add(propertyInfo.Name, propertyInfo.GetValue(obj));

                /*
                if (propertyInfo.PropertyType == typeof(long))
                {
                    long value = (long)propertyInfo.GetValue(obj);
                    expando.Add(propertyInfo.Name, value.ToString());
                }
                else if (propertyInfo.PropertyType == typeof(long?))
                {
                    long? value = (long?)propertyInfo.GetValue(obj);
                    if (value.HasValue)
                        expando.Add(propertyInfo.Name, value.ToString());
                    else
                        expando.Add(propertyInfo.Name, null);
                }
                else if (propertyInfo.PropertyType == typeof(DateTime))
                {
                    DateTime value = (DateTime)propertyInfo.GetValue(obj);
                    if (propertyInfo.GetCustomAttribute(typeof(DateAttribute)) != null)
                        expando.Add(propertyInfo.Name, value.ToDateString());
                    else
                        expando.Add(propertyInfo.Name, value.ToFullDateTimeString());
                }
                else if (propertyInfo.PropertyType == typeof(DateTime?))
                {
                    DateTime? value = (DateTime?)propertyInfo.GetValue(obj);
                    if (value.HasValue)
                    {
                        if (propertyInfo.GetCustomAttribute(typeof(DateAttribute)) != null)
                            expando.Add(propertyInfo.Name, value.ToDateString());
                        else
                            expando.Add(propertyInfo.Name, value.ToFullDateTimeString());
                    }
                    else
                        expando.Add(propertyInfo.Name, null);
                }
                else if (propertyInfo.PropertyType.IsEnum)
                {
                    object value = propertyInfo.GetValue(obj);
                    expando.Add(propertyInfo.Name, value);

                    FieldInfo fi = propertyInfo.PropertyType.GetField(value.ToString());
                    EnumTextAttribute[] attrs = (EnumTextAttribute[])fi.GetCustomAttributes(typeof(EnumTextAttribute));

                    string text = value.ToString();
                    if (attrs.Count() > 0)
                        text = attrs.First().Value;
                    if (expando.ContainsKey(propertyInfo.Name + "Text"))
                        throw new ValidationException(string.Format("{0}类实例调用ToDynamic扩展方法处理枚举属性{1}时发生异常：" +
                                                                    "为实例添加枚举对应文本属性{2}时冲突，类中已有名称为{2}的属性",
                            obj.GetType().Name, propertyInfo.Name, propertyInfo.Name + "Text"));
                    expando.Add(propertyInfo.Name + "Text", text);
                }
                else if (propertyInfo.PropertyType.IsGenericType
                         && propertyInfo.PropertyType.GetGenericTypeDefinition() == typeof(Nullable<>)
                         && propertyInfo.PropertyType.GetGenericArguments().Count() == 1
                         && propertyInfo.PropertyType.GetGenericArguments()[0].IsEnum)
                {
                    object value = propertyInfo.GetValue(obj);
                    expando.Add(propertyInfo.Name, value);

                    string text = string.Empty;
                    if (value != null)
                    {
                        text = value.ToString();

                        FieldInfo fi = propertyInfo.PropertyType.GetGenericArguments()[0].GetField(value.ToString());
                        if (fi != null)
                        {
                            EnumTextAttribute[] attrs =
                                (EnumTextAttribute[])fi.GetCustomAttributes(typeof(EnumTextAttribute));
                            if (attrs.Count() > 0)
                                text = attrs.First().Value;
                        }
                    }

                    if (expando.ContainsKey(propertyInfo.Name + "Text"))
                        throw new ValidationException(string.Format("{0}类实例调用ToDynamic扩展方法处理可空枚举属性{1}时发生异常：" +
                                                                    "为实例添加枚举对应文本属性{2}时冲突，类中已有名称为{2}的属性",
                            obj.GetType().Name, propertyInfo.Name, propertyInfo.Name + "Text"));
                    expando.Add(propertyInfo.Name + "Text", text);
                }
                else
                {
                    try
                    {
                        expando.Add(propertyInfo.Name, propertyInfo.GetValue(obj));
                    }
                    catch (Exception ex)
                    {
                        throw new ValidationException(propertyInfo.Name + "：" + ex.Message, ex);
                    }
                }
                */
            }

            //return expando as ExpandoObject;

            // 直接返回 expando，类型已确定为 ExpandoObject，不会为 null
            return (ExpandoObject)expando;
        }
    }

}

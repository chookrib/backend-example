using src.utility;

namespace src.application
{
    /**
     * 排序项
     */
    public class QuerySortOption
    {
        public string Field
        { get; private set; }

        public string Order
        {
            get; private set;
        }


        public QuerySortOption(string field, string order)
        {
            if (ValueUtility.IsEmptyString(field))
                throw new ApplicationException("排序字段不能为空");

            if (!string.Equals(order, "asc", StringComparison.OrdinalIgnoreCase) &&
                !string.Equals(order, "desc", StringComparison.OrdinalIgnoreCase))
                throw new ApplicationException("排序方向只能为 asc 或 desc");

            Field = field;
            Order = order.ToUpper();
        }
    }
}

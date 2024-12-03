namespace 策略模式Demo.Factory
{
    /// <summary>
    /// 表示一个带有多个条件和一个 key 的结构体。
    /// </summary>
    internal struct ConditionWithKey
    {
        /// <summary>
        /// 获取或设置多个条件，以字典形式存储。
        /// </summary>
        public Dictionary<string, object> Conditions { get; set; }

        /// <summary>
        /// 获取或设置一个字符串，表示该结构体的 key。
        /// </summary>
        public string Key { get; set; }

        public bool IsMatchConditions(object? obj)
        {
            if (obj == null || Conditions == null) return false;

            // 使用反射获取 obj 对象的属性
            var objType = obj.GetType();
            var objProperties = objType.GetProperties();

            // 遍历条件
            foreach (var item in Conditions)
            {
                // 遍历属性
                foreach (var property in objProperties)
                {
                    // 判断属性名称是否与条件中的 key 匹配
                    if (!string.Equals(property.Name, item.Key, StringComparison.OrdinalIgnoreCase)) continue;
                    
                    // 如果是 nullable 类型，则使用 Nullable.GetValueOrDefault 方法获取值
                    var value = property.PropertyType.IsGenericType &&
                                property.PropertyType.GetGenericTypeDefinition() == typeof(Nullable<>) ?
                        property.GetValue(obj)?.GetType().GetMethod("GetValueOrDefault")?.Invoke(property.GetValue(obj), null) :
                        property.GetValue(obj);

                    // 比较属性值和条件值是否相等
                    if (!(value?.Equals(item.Value) ?? false)) return false;
                }
            }

            return true;
        }
    }
}

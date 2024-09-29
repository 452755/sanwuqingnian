using System.Reflection;

namespace 策略模式Demo.Helper
{
    /// <summary>
    /// 提供一些反射相关的辅助方法，包括根据类型名称生成对象实例，以及判断类型是否继承自某个类或接口等。
    /// </summary>
    public static class ReflectionHelper
    {
        /// <summary>
        /// 根据类名字符串获取对应 Type，并判断是否继承自指定的基类或实现指定的接口。
        /// </summary>
        /// <typeparam name="T">基类或接口类型。</typeparam>
        /// <param name="className">类名字符串。</param>
        /// <param name="parameters">参数数组。</param>
        /// <returns>生成的对象实例。</returns>
        /// <exception cref="ArgumentNullException">当 className 参数为 null 或空字符串时抛出。</exception>
        /// <exception cref="ArgumentException">当 className 对应的 Type 不继承自指定的基类或实现指定的接口时抛出。</exception>
        /// <exception cref="MissingMethodException">当 className 对应的 Type 没有无参构造函数时抛出。</exception>
        /// <exception cref="InvalidCastException">当 className 对应的 Type 无法转换为指定的基类或接口类型时抛出。</exception>
        public static T CreateInstance<T>(string className, object[]? parameters = null) where T : class
        {
            // 判断 className 是否为 null 或空字符串，如果是则抛出异常
            if (string.IsNullOrEmpty(className))
            {
                throw new ArgumentNullException(nameof(className));
            }

            // 获取 className 对应的 Type
            var type = Type.GetType(className);

            // 判断 type 是否为 null，如果是则抛出异常
            if (type == null) throw new ArgumentException($"Cannot find type: {className}");

            // 判断 type 是否继承自指定的基类或实现指定的接口
            if (!typeof(T).IsAssignableFrom(type)) throw new ArgumentException($"Type '{className}' does not inherit from '{typeof(T).FullName}'");

            // 如果 type 是抽象类或接口，不能直接创建实例，抛出异常
            if (type.IsAbstract || type.IsInterface) throw new ArgumentException($"Type '{className}' is an abstract class or interface and cannot be instantiated.");

            // 获取对应的构造函数
            var constructor = type.GetConstructor(Type.EmptyTypes);

            // 如果存在参数，则获取对应的构造函数
            if (parameters != null) constructor = type.GetConstructor(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic, null, CallingConventions.HasThis, GetTypes(parameters), null); ;

            // 如果 constructor 为 null，说明不存在对应的构造函数，抛出异常
            if (constructor == null) throw new MissingMethodException($"Constructor not found for type: {className}");

            // 创建实例
            T instance;
            try
            {
                instance = (T)constructor.Invoke(parameters);
            }
            catch (InvalidCastException e)
            {
                throw new InvalidCastException($"Cannot cast type '{className}' to '{typeof(T).FullName}'", e);
            }

            // 判断生成的实例是否为 null，如果是则抛出异常
            if (instance == null)
            {
                throw new InvalidOperationException($"Instance not created for type: {className}");
            }

            // 返回实例
            return instance;
        }

        /// <summary>
        /// 获取参数类型数组。
        /// </summary>
        /// <param name="args">参数数组</param>
        /// <returns>参数类型数组</returns>
        private static Type[] GetTypes(object[]? args)
        {
            //如果参数为空则返回一个空数组
            if (args == null) return Array.Empty<Type>();
            
            var types = new Type[args.Length];
            for (var i = 0; i < args.Length; i++)
            {
                types[i] = args[i].GetType();
            }
            
            return types;
        }
    }
}
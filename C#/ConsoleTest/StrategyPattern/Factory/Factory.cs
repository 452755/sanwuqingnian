using 策略模式Demo.Helper;
using 策略模式Demo.Strategy;

namespace 策略模式Demo.Factory
{
    /// <summary>
    /// 计费规则计算类工厂
    /// </summary>
    public class Factory
    {
        /// <summary>
        /// key和实际策略对象的缓存 
        /// </summary>
        private readonly Dictionary<string, IStrategy?> _keyStrategy = new();

        /// <summary>
        /// 条件和key的缓存对象
        /// </summary>
        private readonly List<ConditionWithKey> _conditionWithKeys = new();

        #region Instance

        public static Factory Instance { get; } = new();

        private Factory() 
        {
            InitConditionWithKeys();
        }
        #endregion

        /// <summary>
        /// 生成具体的计算类对象
        /// </summary>
        /// <param name="timingRule">需要获取计算类的计费规则对象</param>
        /// <returns>具体的计算类对象</returns>
        public IStrategy? GenerateObject(object? timingRule) 
        {
            IStrategy? strategy = null;

            foreach (var item in _conditionWithKeys)
            {
                var isMatch = item.IsMatchConditions(timingRule);
                if (!isMatch) continue;
                
                var key = item.Key;
                if (_keyStrategy.ContainsKey(key)) strategy = _keyStrategy[key];
                else
                {
                    strategy = CreateStrategyInstance(key);
                    _keyStrategy.Add(key, strategy);
                }
            }

            return strategy;
        }

        /// <summary>
        /// 生成具体的策略类对象
        /// </summary>
        /// <param name="strategyTypeName">策略类的名称</param>
        /// <returns></returns>
        private IStrategy? CreateStrategyInstance(string strategyTypeName) 
        {
            try
            {
                return ReflectionHelper.CreateInstance<IStrategy>(strategyTypeName);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return null;
            }
        }

        /// <summary>
        /// 初始化条件和key结构
        /// </summary>
        private void InitConditionWithKeys() 
        {
            const string calculationNameSpace = "";

            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>() 
                {
                    { "TimingRuleType", 4 }
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 0 }
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 1 }
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 2 }
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 3 },
                    { "TimePeriodType", 0}
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 3 },
                    { "TimePeriodType", 1}
                },
                Key = calculationNameSpace + ""
            });
            
            _conditionWithKeys.Add(new ConditionWithKey()
            {
                Conditions = new Dictionary<string, object>()
                {
                    { "TimingRuleType", 3 },
                    { "TimePeriodType", 2}
                },
                Key = calculationNameSpace + ""
            });
        }
    }
}

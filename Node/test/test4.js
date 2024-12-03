// 将某个时间的日期修改为另一个时间的日期
function setDateToToday(date, now) {
    date.setFullYear(now.getFullYear())
    date.setMonth(now.getMonth())
    date.setDate(now.getDate());
  }
  
  function getcurrentMealTimeList(now) {
    const nowMill = now.getTime()
    const mealTimeList = JSON.parse(localStorage.meal_time_data)
  
    if (mealTimeList === undefined || mealTimeList === null || mealTimeList.length === 0) 
    {
        return null
    }
  
    // 遍历市别列表
    for (let index = 0; index < mealTimeList.length; index++) {
      // 转换市别的开始时间和结束时间
      let startTime = (new Date(Number(mealTimeList[index].start) * 1000))
      let endTime = (new Date(Number(mealTimeList[index].end) * 1000))
  
      // 修改市别的时间的日期为当前时间的日期
      setDateToToday(startTime, now)
      setDateToToday(endTime, now)
  
      // 获取时间戳
      let start = startTime.getTime()
      let end = endTime.getTime()
  
      // 如果开始时间小于当前时间则表示没跨天
      if (start < end) 
      {
        // 直接判断当前时间戳是否在市别内
        if (start <= nowMill && nowMill < end) 
        {
          return mealTimeList[index]
        }
      }
  
      // 处理跨天相关逻辑
      // 将开始时间往前拨一天
      let newStartTime = new Date(start);
      newStartTime.setDate(newStartTime.getDate() - 1)
  
      // 直接判断当前时间戳是否在市别内
      if (newStartTime.getTime() <= nowMill && nowMill < end) 
      {
        return mealTimeList[index]
      }
  
      let newEndTime = new Date(end);
      newEndTime.setDate(newEndTime.getDate() + 1)
      
      // 直接判断当前时间戳是否在市别内
      if (start <= nowMill && nowMill < newEndTime.getTime()) 
      {
        return mealTimeList[index]
      }
    }
  }
  
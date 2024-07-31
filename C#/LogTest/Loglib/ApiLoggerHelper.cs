using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using TZXCommonFunction.Common;
using TzxLocalServerLib.Common;

namespace TzxLocalServerLib.AppManager.Common
{
    /// <summary>
    /// 日志状态
    /// </summary>
    public enum LoggerStatus
    {
        /// <summary>
        /// 无状态
        /// </summary>
        None = 0,
        /// <summary>
        /// 启动中
        /// </summary>
        Starting = 1,
        /// <summary>
        /// 启动完成
        /// </summary>
        Started = 1,
        /// <summary>
        /// 关闭中
        /// </summary>
        Stoping = 2,
        /// <summary>
        /// 关闭完成
        /// </summary>
        Stoped = 3,
    }

    public class ApiLoggerHelper
    {
        /// <summary>
        /// 日志信息
        /// </summary>
        private class LoggerInfo
        {
            public string logContent;

            public DateTime outputTime;
        }

        #region 日志参数
        private const string v_log_section_ame = "AppServiceLog";

        // 接口是否输出正常日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_info_log = "i_app_api_output_info_log";
        // 接口是否输出异常日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_error_log = "i_app_api_output_error_log";
        // 接口是否输出控制台日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_console_log = "i_app_api_output_console_log";

        // 接口日志文件的最大大小 单位KB 默认值 2048KB 2MB
        private const string i_app_api_log_file_max_size = "i_app_api_log_file_max_size";
        // 日志文件滚动的数量 默认为10
        private const string i_app_api_log_file_max_rolling_num = "i_app_api_log_file_max_rolling_num";

        private static bool isOutputInfoLog = getIsOutputLog(i_app_api_output_info_log);
        private static bool isOutputErrorLog = getIsOutputLog(i_app_api_output_error_log);
        private static bool isOutputConsoleLog = getIsOutputLog(i_app_api_output_console_log);

        private static int logFileMaxSize = getLogFileProp(i_app_api_log_file_max_size);
        private static int logFileMaxRollingNum = getLogFileProp(i_app_api_log_file_max_rolling_num, 10);

        /// <summary>
        /// 是否输出正常日志
        /// </summary>
        public static bool IsOutputInfoLog
        {
            get
            {
                return isOutputInfoLog;
            }
            set
            {
                if (isOutputInfoLog != value)
                {
                    isOutputInfoLog = value;
                    saveIsOutputLog(i_app_api_output_info_log, value);
                }
            }
        }

        /// <summary>
        /// 是否输出异常日志
        /// </summary>
        public static bool IsOutputErrorLog
        {
            get
            {
                return isOutputErrorLog;
            }
            set
            {
                if (isOutputErrorLog != value)
                {
                    isOutputErrorLog = value;
                    saveIsOutputLog(i_app_api_output_error_log, value);
                }
            }
        }

        /// <summary>
        /// 是否输出控制台日志
        /// </summary>
        public static bool IsOutputConsoleLog
        {
            get
            {
                return isOutputConsoleLog;
            }
            set
            {
                if (isOutputConsoleLog != value)
                {
                    isOutputConsoleLog = value;
                    saveIsOutputLog(i_app_api_output_console_log, value);
                }
            }
        }

        /// <summary>
        /// 接口日志文件的最大大小 单位KB 默认值 2048KB 2MB
        /// </summary>
        public static int LogFileMaxSize
        {
            get
            {
                return logFileMaxSize;
            }
            set
            {
                if (logFileMaxSize != value)
                {
                    logFileMaxSize = value;
                    saveLogFileMaxSize(value);
                }
            }
        }

        /// <summary>
        /// 接口日志文件的滚动数量 默认为10
        /// </summary>
        public static int LogFileMaxRollingNum 
        {
            get 
            {
                return logFileMaxRollingNum; 
            }
            set 
            {
                if (logFileMaxRollingNum != value) 
                {
                    logFileMaxRollingNum = value;
                    saveLogFileProp(i_app_api_log_file_max_rolling_num, value);
                }
            }
        }

        private static bool getIsOutputLog(string logKey)
        {
            IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            string logValue = iniFileManager.iniReadValue(v_log_section_ame, logKey);

            if (string.IsNullOrEmpty(logValue) == true)
            {
                saveIsOutputLog(logKey, false);

                return false;
            }

            return logValue == "1";
        }

        private static void saveIsOutputLog(string logKey, bool logValue)
        {
            IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            iniFileManager.iniWriteValue(v_log_section_ame, logKey, logValue ? "1" : "0");
        }

        private static int getLogFileProp(string logKey, int defaultValue = 0)
        {
            IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            string logValue = iniFileManager.iniReadValue(v_log_section_ame, logKey);

            if (string.IsNullOrEmpty(logValue) == true)
            {
                saveLogFileProp(logKey, defaultValue);

                return defaultValue;
            }

            return int.Parse(logValue);
        }

        private static void saveLogFileProp(string logKey, int propValue)
        {
            IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            iniFileManager.iniWriteValue(v_log_section_ame, logKey, propValue.ToString());
        }
        #endregion

        #region 写入日志

        /// <summary>
        /// 写入 Info 日志 "%d [%t] Info  %c %m%n"
        /// </summary>
        /// <param name="t">调用所在类型</param>
        /// <param name="msg">日志内容</param>
        public static void WriteInfoLog(Type t, string msg)
        {
            if (isOutputInfoLog == false)
            {
                return;
            }

            // 如果状态是关闭或关闭中，则不写入日志
            if (loggerStatus == LoggerStatus.None || loggerStatus == LoggerStatus.Stoped || loggerStatus == LoggerStatus.Stoping) 
            {
                return;
            }

            string logStr = string.Format("{0} [{1}] Info {2} {3}", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.fff"), Thread.CurrentThread.ManagedThreadId, t.FullName, msg);

            logQueue.Enqueue(new LoggerInfo() { outputTime = DateTime.Now, logContent = logStr });
        }

        /// <summary>
        /// 写入 Error 日志 "%d [%t] Error %c %m%n 错误信息： %n 调用堆栈："
        /// </summary>
        /// <param name="t">调用所在类型</param>
        /// <param name="msg">日志内容</param>
        /// <param name="ex">异常信息</param>
        public static void WriteErrorLog(Type t, string msg, Exception ex)
        {
            if (isOutputErrorLog == false)
            {
                return;
            }

            // 如果状态是关闭或关闭中，则不写入日志
            if (loggerStatus == LoggerStatus.None || loggerStatus == LoggerStatus.Stoped || loggerStatus == LoggerStatus.Stoping)
            {
                return;
            }

            string logStr = string.Format("{0} [{1}] Error {2} {3} \r\n 错误信息：{4} \r\n 调用堆栈：{5}", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.fff"), Thread.CurrentThread.ManagedThreadId, t.FullName, msg, ex.Message, ex.StackTrace);

            logQueue.Enqueue(new LoggerInfo() { outputTime = DateTime.Now, logContent = logStr });
        }

        #endregion

        #region 日志处理
        private readonly static string logNameStart = "员工助理App";
        private readonly static string logExtensionName = ".log";

        // 日志输出队列
        private readonly static ConcurrentQueue<LoggerInfo> logQueue = new ConcurrentQueue<LoggerInfo>();
        // 日志输出线程
        private static Thread writeLogThread = null;
        // 是否需要停止输出日志
        private static bool isNeedStopWriteLog = false;

        // 当前输出日志内容
        private static string currentLogContent = "";
        /// <summary>
        /// 当前输出日志内容
        /// </summary>
        public static string CurrentLogContent { get { return currentLogContent; } }

        // 最后50条日志内容
        private static ConcurrentQueue<string> lastFiftyLog = new ConcurrentQueue<string>();
        /// <summary>
        /// 最后50条日志内容
        /// </summary>
        public static IEnumerable<string> LastFiftyLog { get { return lastFiftyLog; } }

        // 当前输出日志的文件名
        private static string currentLogFileName = "";

        // 当前日志文件的文件流
        private static FileStream logFileStream;
        // 当前写入日志的监听器
        private static TextWriterTraceListener traceListener;

        // 日志当前状态
        private static LoggerStatus loggerStatus = LoggerStatus.None;

        /// <summary>
        /// 日志当前状态
        /// </summary>
        public static LoggerStatus LoggerStatus { get{ return loggerStatus; } }

        /// <summary>
        /// 是否初始化完成
        /// </summary>
        public static bool IsInitLogger { get { return loggerStatus == LoggerStatus.Started; } }

        // 当前文件滚动的次数
        private static int m_curSizeRollBackups = 0;
        /// <summary>
        /// 当前日志文件滚动的次数
        /// </summary>
        public static int CurrentSizeRollBackups { get { return m_curSizeRollBackups; } }

        /// <summary>
        /// 开启日志
        /// </summary>
        public static void StartApiLog()
        {
            if (loggerStatus == LoggerStatus.Stoping) 
            {
                throw new InvalidOperationException("当前日志正在关闭，无法进行启动");
            }

            if (loggerStatus == LoggerStatus.Starting || loggerStatus == LoggerStatus.Started) 
            {
                return;
            }

            try
            {
                loggerStatus = LoggerStatus.Starting;

                DateTime nowTime = DateTime.Now;

                InitializeRollBackups(getCurrentLogFileName(nowTime));

                initCurrentLogFileStream(getCurrentLogFileName(nowTime));

                writeLogThread = new Thread(writeLogThreadFunction);
                writeLogThread.Start();
            }
            catch (Exception ex)
            {
                loggerStatus = LoggerStatus.None;
                isNeedStopWriteLog = false;
                throw ex;
            }
        }

        /// <summary>
        /// 关闭日志
        /// </summary>
        public static void StopApiLog()
        {
            if (loggerStatus == LoggerStatus.None || loggerStatus == LoggerStatus.Starting) 
            {
                throw new InvalidOperationException("当前日志正在启动，无法进行关闭");
            }

            if (loggerStatus == LoggerStatus.Stoped || loggerStatus == LoggerStatus.Stoping) 
            {
                return;
            }

            loggerStatus = LoggerStatus.Stoping;

            isNeedStopWriteLog = true;

            if (writeLogThread != null && writeLogThread.IsAlive)
            {
                writeLogThread.Join();
            }

            isNeedStopWriteLog = false;

            closeCurrentLogFileStream();

            loggerStatus = LoggerStatus.Stoped;
        }

        /// <summary>
        /// 写日志
        /// </summary>
        private static void writeLogThreadFunction()
        {
            loggerStatus = LoggerStatus.Started;
            isNeedStopWriteLog = false;

            // 一直循环进行输出日志
            while (true)
            {
                // 如果队列中没有日志内容，并且需要停止时
                LoggerInfo loggerInfo = null;
                if (logQueue.TryDequeue(out loggerInfo) == false) 
                {
                    if (isNeedStopWriteLog == true) 
                    {
                        return;
                    }
                }

                writeLogInfo(loggerInfo);
            }
        }

        /// <summary>
        /// 写入日志详情
        /// </summary>
        /// <param name="loggerInfo"></param>
        private static void writeLogInfo(LoggerInfo loggerInfo)
        {
            if (loggerInfo == null)
            {
                return;
            }

            lock (currentLogContent)
            {
                currentLogContent = loggerInfo.logContent;

                if (lastFiftyLog.Count >= 50) 
                {
                    lastFiftyLog.TryDequeue(out _);
                    lastFiftyLog.Enqueue(currentLogContent);
                }
            }

            // 如果有输出控制台日志
            if (isOutputConsoleLog == true)
            {
                Console.WriteLine(loggerInfo.logContent);
            }

            // 判断当前日志需要输出的文件是否和当前存储的文件名称一样，也就是跨天了
            string currentLogLogFileName = getCurrentLogFileName(loggerInfo.outputTime);
            if (currentLogLogFileName != currentLogFileName)
            {
                m_curSizeRollBackups = 0;
                closeCurrentLogFileStream();
                // 按照新的日志文件名称初始化文件流
                initCurrentLogFileStream(currentLogLogFileName);
            }

            // 判断当前文件大小是否超出了设置的文件大小，超过了则需要滚动文件
            if (logFileStream.Length >= logFileMaxSize * 1024)
            {
                rollLogFile(currentLogLogFileName);
            }

            // 输出日志
            traceListener.WriteLine(currentLogContent);
            traceListener.Flush();
        }

        /// <summary>
        /// 获取当前日志文件夹
        /// </summary>
        /// <returns></returns>
        private static string getLogFileDirectory()
        {
            string text = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Log");
            
            if (!Directory.Exists(text))
            {
                Directory.CreateDirectory(text);
            }

            return text;
        }

        /// <summary>
        /// 获取当前日志文件名称
        /// </summary>
        /// <returns></returns>
        private static string getCurrentLogFileName(DateTime dateTime)
        {
            return logNameStart + "_" + dateTime.ToString("yyyy-MM-dd");
        }

        /// <summary>
        /// 初始化日志文件流
        /// </summary>
        /// <param name="fileName"></param>
        private static void initCurrentLogFileStream(string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
            {
                throw new ArgumentException("日志文件名称不能为空", nameof(fileName));
            }

            currentLogFileName = fileName;
            string path = Path.Combine(getLogFileDirectory(), fileName + logExtensionName);

            logFileStream = new FileStream(path, FileMode.Append, FileAccess.Write, FileShare.Read);

            traceListener = new TextWriterTraceListener(new StreamWriter(logFileStream, Encoding.UTF8));
            Trace.Listeners.Add(traceListener);
        }

        /// <summary>
        /// 关闭当前日志文件流
        /// </summary>
        private static void closeCurrentLogFileStream() 
        {
            if (traceListener != null)
            {
                traceListener.Close();
                traceListener = null;
            }
            if (logFileStream != null)
            {
                logFileStream.Close();
                logFileStream = null;
            }
        }

        /// <summary>
        /// 日志文件滚动
        /// </summary>
        /// <param name="fileName"></param>
        private static void rollLogFile(string fileName)
        {
            closeCurrentLogFileStream();

            //// 获取日志文件夹信息
            //DirectoryInfo logDirectoryInfo = new DirectoryInfo(getLogFileDirectory());
            //FileInfo[] files = logDirectoryInfo.GetFiles();

            //// 如果有日志文件
            //if (files != null && files.Length > 0)
            //{
            //    List<FileInfo> fileInfos = new List<FileInfo>();

            //    foreach (FileInfo file in files)
            //    {
            //        if (file.Name.StartsWith(fileName))
            //        {
            //            fileInfos.Add(file);
            //        }
            //    }

            //    if (fileInfos.Count > 0)
            //    {
            //        fileInfos.Sort((f1, f2) =>
            //        {
            //            if (f1 == null && f2 == null)
            //            {
            //                return 0;
            //            }

            //            if (f1 == null)
            //            {
            //                return -1;
            //            }

            //            if (f2 == null)
            //            {
            //                return 1;
            //            }

            //            int f1Num = 0;
            //            int f2Num = 0;

            //            if (f1.Name.Split('.').Length == 3)
            //            {
            //                f1Num = int.Parse(f1.Name.Split('.')[1]);
            //            }

            //            if (f2.Name.Split('.').Length == 3)
            //            {
            //                f2Num = int.Parse(f2.Name.Split('.')[1]);
            //            }

            //            if (f1Num == f2Num)
            //            {
            //                return 0;
            //            }

            //            return f2Num.CompareTo(f1Num);
            //        });

            //        foreach (FileInfo file in fileInfos)
            //        {
            //            string[] fileNameSplit = file.Name.Split('.');
            //            if (fileNameSplit.Length == 3)
            //            {
            //                int logFileNum = 0;
            //                if (int.TryParse(fileNameSplit[1], out logFileNum) == true)
            //                {
            //                    logFileNum += 1;
            //                    fileNameSplit[1] = logFileNum.ToString();

            //                    string newFileName = Path.Combine(getLogFileDirectory(), string.Join(".", fileNameSplit));

            //                    file.MoveTo(newFileName);
            //                }
            //                continue;
            //            }

            //            if (fileNameSplit.Length == 2)
            //            {
            //                string newFileName = Path.Combine(getLogFileDirectory(), fileName + ".1" + logExtensionName);

            //                file.MoveTo(newFileName);
            //                continue;
            //            }
            //        }
            //    }
            //}


            string currentLogFilePath = Path.Combine(getLogFileDirectory(), fileName);

            for (int i = m_curSizeRollBackups; i >= 1; i--)
            {
                string toMove = currentLogFilePath + "." + i + logExtensionName;
                if (File.Exists(toMove))
                {
                    // 如果最大滚动数等于设置的最大滚动数量，则将最大的滚动文件删除
                    if (i == LogFileMaxRollingNum)
                    {
                        File.Delete(toMove);
                        continue;
                    }

                    // 滚动文件
                    string moveTo = currentLogFilePath + "." + (i + 1) + logExtensionName;
                    File.Move(toMove, moveTo);
                }
            }
            File.Move(currentLogFilePath + logExtensionName, currentLogFilePath + ".1" + logExtensionName);

            if (m_curSizeRollBackups < logFileMaxRollingNum)
            {
                m_curSizeRollBackups++;
            }

            // 按照新的日志文件名称初始化文件流
            initCurrentLogFileStream(fileName);
        }

        /// <summary>
        /// 初始化当前的文件最大滚动次数
        /// </summary>
        /// <param name="fileName"></param>
        private static void InitializeRollBackups(string fileName)
        {
            string directory = getLogFileDirectory();
            string[] files = Directory.GetFiles(directory, fileName + "*" + logExtensionName);

            int maxBackupIndex = 0;

            foreach (string file in files)
            {
                string extension = file.Substring((directory + Path.DirectorySeparatorChar + fileName).Length).TrimEnd(logExtensionName.ToCharArray());
                int backupIndex = ParseSuffix(extension);
                if (backupIndex > maxBackupIndex)
                {
                    maxBackupIndex = backupIndex;
                }
            }

            m_curSizeRollBackups = maxBackupIndex;
        }

        /// <summary>
        /// 转化文件的滚动次数
        /// </summary>
        /// <param name="extension">文件滚动次数字符串</param>
        /// <returns></returns>
        private static int ParseSuffix(string extension)
        {
            if (extension.StartsWith("."))
            {
                extension = extension.Substring(1);
            }

            int backupIndex;
            if (int.TryParse(extension, out backupIndex))
            {
                return backupIndex;
            }

            return 0;
        }
        #endregion
    }
}

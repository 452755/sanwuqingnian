using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;

namespace TzxLocalServerLib.AppManager.Common
{
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
        // 接口是否输出正常日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_info_log = "i_app_api_output_info_log";
        // 接口是否输出异常日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_error_log = "i_app_api_output_error_log";
        // 接口是否输出控制台日志 0：不输出；1：输出；默认为：0
        private const string i_app_api_output_console_log = "i_app_api_output_console_log";

        // 接口日志文件的最大大小 单位KB 默认值 2048KB 2MB
        private const string i_app_api_log_file_max_size = "i_app_api_log_file_max_size";

        private static bool isOutputInfoLog = getIsOutputLog(i_app_api_output_info_log);
        private static bool isOutputErrorLog = getIsOutputLog(i_app_api_output_error_log);
        private static bool isOutputConsoleLog = getIsOutputLog(i_app_api_output_console_log);

        private static int logFileMaxSize = getLogFileMaxSize();

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

        private static bool getIsOutputLog(string logKey)
        {
            //IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            //string logValue = iniFileManager.iniReadValue("AppServiceLog", logKey);

            //if (string.IsNullOrEmpty(logValue) == true)
            //{
            //    saveIsOutputLog(logKey, false);

            //    return false;
            //}

            //return logValue == "1";

            return true;
        }

        private static void saveIsOutputLog(string logKey, bool logValue)
        {
            //IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            //iniFileManager.iniWriteValue("AppServiceLog", logKey, logValue ? "1" : "0");
        }

        private static int getLogFileMaxSize()
        {
            //IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            //string logValue = iniFileManager.iniReadValue("AppServiceLog", i_app_api_log_file_max_size);

            //if (string.IsNullOrEmpty(logValue) == true)
            //{
            //    saveLogFileMaxSize(2048);

            //    return 2048;
            //}

            //return int.Parse(logValue);

            return 1;
        }

        private static void saveLogFileMaxSize(int fileMaxSize)
        {
            //IniFileManager iniFileManager = new IniFileManager(UtilFunction.getLocalServerIniFilePath());
            //iniFileManager.iniWriteValue("AppServiceLog", i_app_api_log_file_max_size, fileMaxSize.ToString());
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

            string logStr = string.Format("{0} [{1}] Console-Error {2} {3} \r\n 错误信息：{4} \r\n 调用堆栈：{5}", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.fff"), Thread.CurrentThread.ManagedThreadId, t.FullName, msg, ex.Message, ex.StackTrace);

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
        // 是否停止输出日志
        private static bool isStopWriteLog = false;

        // 是否正在滚动日志文件
        private static bool isRolling = false;
        // 滚动日志文件时的日志内容
        private static List<string> rollingLogs = new List<string>();

        // 当前输出日志内容
        private static string currentLogContent = "";
        public static string CurrentLogContent { get { return currentLogContent; } }

        // 最后50条日志内容
        private static List<string> lastFiftyLog = new List<string>();
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

        // 是否初始化完成
        private static bool isInitLogger = false;
        /// <summary>
        /// 是否初始化完成
        /// </summary>
        public static bool IsInitLogger { get { return isInitLogger; } }

        /// <summary>
        /// 初始化日志
        /// </summary>
        public static void InitApiLog()
        {
            if (isInitLogger == true)
            {
                return;
            }

            try
            {
                initCurrentLogFileStream(getCurrentLogFileName(DateTime.Now));

                writeLogThread = new Thread(writeLogThreadFunction);
                writeLogThread.Start();

                isInitLogger = true;
                isStopWriteLog = false;
                isRolling = false;
            }
            catch (Exception ex)
            {
                isInitLogger = false;
                throw ex;
            }
        }

        /// <summary>
        /// 反初始化日志
        /// </summary>
        public static void DeInitApiLog()
        {
            isStopWriteLog = true;
        }

        /// <summary>
        /// 写日志
        /// </summary>
        private static void writeLogThreadFunction()
        {
            // 一直循环进行输出日志
            while (true)
            {
                if (isStopWriteLog == true)
                {
                    traceListener.Close();
                    isInitLogger = false;
                    isRolling = false;
                    isStopWriteLog = false;
                    break;
                }

                writeLogInfo();
            }
        }
        private static void writeLogInfo()
        {
            LoggerInfo loggerInfo = null;

            if (logQueue.TryDequeue(out loggerInfo))
            {
                if (loggerInfo == null)
                {
                    return;
                }

                lock (currentLogContent)
                {
                    currentLogContent = loggerInfo.logContent;
                }

                // 如果有输出控制台日志
                if (isOutputConsoleLog == true)
                {
                    Console.WriteLine(loggerInfo.logContent);
                }

                // 如果正在滚动文件
                if (isRolling == true)
                {
                    lock (rollingLogs)
                    {
                        rollingLogs.Add(loggerInfo.logContent);
                    }
                    return;
                }

                // 判断当前日志需要输出的文件是否和当前存储的文件名称一样，也就是跨天了
                string currentLogLogFileName = getCurrentLogFileName(loggerInfo.outputTime);
                if (currentLogLogFileName != currentLogFileName)
                {
                    lock (rollingLogs)
                    {
                        rollingLogs.Add(loggerInfo.logContent);
                    }
                    rollLogFile(currentLogLogFileName);
                    return;
                }

                // 判断当前文件大小是否超出了设置的文件大小，超过了则需要滚动文件
                if (logFileStream.Length >= logFileMaxSize * 1024)
                {
                    lock (rollingLogs)
                    {
                        rollingLogs.Add(loggerInfo.logContent);
                    }
                    rollLogFile(currentLogLogFileName);
                    return;
                }

                // 输出日志
                traceListener.WriteLine(currentLogContent);
                traceListener.Flush();
            }
        }

        /// <summary>
        /// 获取当前日志文件夹
        /// </summary>
        /// <returns></returns>
        private static string getLogFileDirectory()
        {
            string text = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Log");
            if (string.IsNullOrEmpty(text))
            {
                text = "Log";
            }

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
                return;
            }

            currentLogFileName = fileName;
            string path = Path.Combine(getLogFileDirectory(), fileName + logExtensionName);

            if (!File.Exists(path))
            {
                logFileStream = new FileStream(path, FileMode.OpenOrCreate, FileAccess.Write, FileShare.Read);
            }
            else
            {
                logFileStream = new FileStream(path, FileMode.Append, FileAccess.Write, FileShare.Read);
            }

            traceListener = new TextWriterTraceListener(new StreamWriter(logFileStream, Encoding.UTF8));
            Trace.Listeners.Add(traceListener);
        }

        /// <summary>
        /// 日志文件滚动
        /// </summary>
        /// <param name="fileName"></param>
        private static void rollLogFile(string fileName)
        {
            isRolling = true;
            new Thread(() =>
            {
                traceListener.Close();
                traceListener.Dispose();
                logFileStream.Close();

                // 获取日志文件夹信息
                DirectoryInfo logDirectoryInfo = new DirectoryInfo(getLogFileDirectory());
                FileInfo[] files = logDirectoryInfo.GetFiles();

                // 如果有日志文件
                if (files != null && files.Length > 0)
                {
                    List<FileInfo> fileInfos = new List<FileInfo>();

                    foreach (FileInfo file in files)
                    {
                        if (file.Name.StartsWith(fileName))
                        {
                            fileInfos.Add(file);
                        }
                    }

                    if (fileInfos.Count > 0)
                    {
                        fileInfos.Sort((f1, f2) =>
                        {
                            if (f1 == null && f2 == null)
                            {
                                return 0;
                            }

                            if (f1 == null)
                            {
                                return -1;
                            }

                            if (f2 == null)
                            {
                                return 1;
                            }

                            int f1Num = 0;
                            int f2Num = 0;

                            if (f1.Name.Split('.').Length == 3)
                            {
                                f1Num = int.Parse(f1.Name.Split('.')[1]);
                            }

                            if (f2.Name.Split('.').Length == 3)
                            {
                                f2Num = int.Parse(f2.Name.Split('.')[1]);
                            }

                            if (f1Num == f2Num)
                            {
                                return 0;
                            }

                            return f2Num.CompareTo(f1Num);
                        });

                        foreach (FileInfo file in fileInfos)
                        {
                            string[] fileNameSplit = file.Name.Split('.');
                            if (fileNameSplit.Length == 3)
                            {
                                int logFileNum = 0;
                                if (int.TryParse(fileNameSplit[1], out logFileNum) == true)
                                {
                                    logFileNum += 1;
                                    fileNameSplit[1] = logFileNum.ToString();

                                    string newFileName = Path.Combine(getLogFileDirectory(), string.Join(".", fileNameSplit));

                                    file.MoveTo(newFileName);
                                }
                                continue;
                            }

                            if (fileNameSplit.Length == 2)
                            {
                                string newFileName = Path.Combine(getLogFileDirectory(), fileName + ".1" + logExtensionName);

                                file.MoveTo(newFileName);
                                continue;
                            }
                        }
                    }
                }

                // 按照新的日志文件名称初始化文件流
                initCurrentLogFileStream(fileName);
                isRolling = false;
            }).Start();
        }
        #endregion
    }
}

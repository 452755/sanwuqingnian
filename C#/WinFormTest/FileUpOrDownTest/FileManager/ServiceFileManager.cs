using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Timers;
using FileUpOrDownTest.Common;

namespace FileUpOrDownTest.FileManager
{
    internal class ServiceFileManager
    {
        private static readonly object _lock = new object();

        private const int checkTransferSessionInterval = 30000;
        private const int transferSessionTimeoutMinutes = 5;

        private const string v_file_fingerprint_key = "FileFingerprint";

        private readonly string fileBaseDirectory = "";

        private static readonly Dictionary<string, FileTransferSession> TransferSessions = new Dictionary<string, FileTransferSession>();

        private class FileTransferSession
        {
            public string TransferId { get; set; }
            public string FileName { get; set; }
            public string FilePath { get; set; }
            public FileStream FileStream { get; set; }
            public DateTime LastActivityTime { get; set; }
            public bool IsUpload { get; set; }
        }

        #region Instance

        private static ServiceFileManager instance = new ServiceFileManager();

        public static ServiceFileManager Instance
        {
            get { return instance; }
        }

        private ServiceFileManager()
        {
            fileBaseDirectory = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "ServerFiles");
            if (Directory.Exists(fileBaseDirectory) == false) 
            {
                Directory.CreateDirectory(fileBaseDirectory);
            }

            System.Timers.Timer timer = new System.Timers.Timer(checkTransferSessionInterval);
            timer.Elapsed += CheckTransferSessions;
            timer.Start();
        }
        #endregion

        #region 文件指纹

        public void UpdateFileFingerprint(string fileName) 
        {
            string fileFingerprint = GenerateFileFingerprint(fileName);
            IniFileManager iniFileManager = new IniFileManager(getLocalServerIniFilePath());
            iniFileManager.iniWriteValue(v_file_fingerprint_key, fileName, fileFingerprint);
        }

        private string getLocalServerIniFilePath()
        {
            string iniFilePath = Path.Combine(fileBaseDirectory, "fileFingerprint.ini");

            if (File.Exists(iniFilePath) == false)
            {
                // 不存在则直接创建缓存文件
                FileStream fileStream = System.IO.File.Create(iniFilePath);
                if (fileStream != null)
                {
                    fileStream.Close();
                }
            }

            return iniFilePath;
        }

        /// <summary>
        /// 获取文件指纹
        /// </summary>
        /// <param name="fileName"></param>
        /// <returns></returns>
        public string GetFileFingerprint(string fileName, out string errMsg)
        {
            lock (_lock)
            {
                try
                {
                    errMsg = "";

                    IniFileManager iniFileManager = new IniFileManager(getLocalServerIniFilePath());

                    string fileFingerprint = null;

                    // 如果文件存在，则获取本地的文件指纹
                    string filePath = Path.Combine(fileBaseDirectory, fileName);
                    if (File.Exists(filePath) == true)
                    {
                        fileFingerprint = iniFileManager.iniReadValue(v_file_fingerprint_key, fileName);

                        if (string.IsNullOrEmpty(fileFingerprint))
                        {
                            fileFingerprint = GenerateFileFingerprint(fileName);
                            iniFileManager.iniWriteValue(v_file_fingerprint_key, fileName, fileFingerprint);
                        }
                    }
                    else
                    {
                        fileFingerprint = GenerateFileFingerprint(fileName, true);
                        iniFileManager.iniWriteValue(v_file_fingerprint_key, fileName, fileFingerprint);
                    }

                    Console.WriteLine(string.Format("获取文件指纹：{0} - {1}", fileName, fileFingerprint));

                    return fileFingerprint;
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    Console.WriteLine("获取文件指纹报错" + ex.Message);
                    return null;
                }
            }
        }

        /// <summary>
        /// 根据文件名称和是否删除生成文件指纹
        /// </summary>
        private string GenerateFileFingerprint(string fileName, bool isDelete = false)
        {
            string content = $"{fileName}{DateTime.Now.ToString("yyyyMMddHHmmssfff")}{new Random().Next(1, 10000):D3}{((isDelete == true) ? "-delete" : " - modify")}";

            return CEncoder.Encode(content);
        }

        #endregion

        #region 处理长时占用文件问题

        private void CheckTransferSessions(object sender, System.Timers.ElapsedEventArgs e)
        {
            lock (_lock)
            {
                var now = DateTime.Now;
                var timeout = TimeSpan.FromMinutes(transferSessionTimeoutMinutes); // 设置超时时间为5分钟

                var sessionsToRemove = TransferSessions
                    .Where(kv => now - kv.Value.LastActivityTime > timeout)
                    .Select(kv => kv.Key)
                    .ToList();

                foreach (var transferId in sessionsToRemove)
                {
                    FileTransferSession session = null;
                    if (TransferSessions.TryGetValue(transferId, out session))
                    {
                        session.FileStream.Close();
                        TransferSessions.Remove(transferId);
                        Console.WriteLine(string.Format("{0}文件 {1}({2})因超时已退出", ((session.IsUpload == true) ? "上传" : "下载"), session.FileName, transferId));
                    }
                }
            }
        }

        #endregion

        #region 上传文件

        /// <summary>
        /// 上传文件
        /// </summary>
        /// <param name="fileName"></param>
        /// <param name="fileStream"></param>
        public string UploadFile(string fileName, byte[] fileStream, out string errMsg)
        {
            lock (_lock)
            {
                try
                {
                    // 判断该文件是否正在进行上传或下载
                    if (TransferSessions.Values.Any(session => session.FileName == fileName))
                    {
                        errMsg = fileName + " 文件正在上传或下载";
                        return "";
                    }

                    errMsg = "";

                    string filePath = Path.Combine(fileBaseDirectory, fileName);

                    // 删除原文件
                    if (File.Exists(filePath) == false)
                    {
                        File.Delete(filePath);
                    }

                    string fileFingerprint = null;

                    // 如果文件流存在，则保存文件
                    if (fileStream != null && fileStream.Length > 0)
                    {
                        using (FileStream stream = new FileStream(filePath, FileMode.Create, FileAccess.Write))
                        {
                            stream.Write(fileStream, 0, fileStream.Length);
                        }

                        fileFingerprint = GenerateFileFingerprint(fileName);
                    }
                    // 如果文件不存在则生成一个删除的文件指纹
                    else
                    {
                        fileFingerprint = GenerateFileFingerprint(fileName, true);
                    }

                    IniFileManager iniFileManager = new IniFileManager(getLocalServerIniFilePath());
                    iniFileManager.iniWriteValue(v_file_fingerprint_key, fileName, fileFingerprint);

                    if (fileName == ClientFileManager.DishesImageFileName)
                    {
                        handleDishesImageFileUpload();
                    }

                    Console.WriteLine(string.Format("上传文件：{0}，生成的文件指纹：{1}", fileName, fileFingerprint));

                    return fileFingerprint;
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    Console.WriteLine("上传文件报错" + ex.Message);
                    return null;
                }
            }
        }

        /// <summary>
        /// 开始上传大文件
        /// </summary>
        /// <param name="fileName">文件名称</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>上传文件的Id</returns>
        public string BeginUploadLargeFiles(string fileName, out string errMsg) 
        {
            lock (_lock)
            {
                try
                {
                    if (TransferSessions.Values.Any(session => session.FileName == fileName))
                    {
                        errMsg = fileName + " 文件正在上传或下载";
                        return "";
                    }

                    errMsg = "";
                    string filePath = Path.Combine(fileBaseDirectory, fileName);
                    // 删除原文件
                    if (File.Exists(filePath) == false)
                    {
                        File.Delete(filePath);
                    }

                    // 创建上传文件Id
                    var transferId = Guid.NewGuid().ToString();
                    // 创建上传文件信息
                    var transferSession = new FileTransferSession
                    {
                        FileName = fileName,
                        TransferId = transferId,
                        IsUpload = true,
                        FilePath = filePath,
                        LastActivityTime = DateTime.Now,
                        FileStream = new FileStream(filePath, FileMode.Create, FileAccess.Write, FileShare.None)
                    };

                    TransferSessions.Add(transferId, transferSession);

                    return transferId;
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    return "";
                }
            }
        }

        /// <summary>
        /// 上传大文件分片
        /// </summary>
        /// <param name="transferId ">上传文件Id</param>
        /// <param name="chunk">文件分片</param>
        /// <param name="chunkNumber">分片批次</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>是否成功</returns>
        public bool UploadLargeFileChunk(string transferId, int chunkStart, byte[] chunk, out string errMsg)
        {
            try
            {
                if (TransferSessions.ContainsKey(transferId) == false)
                {
                    errMsg = "无效的上传文件Id：" + transferId ;
                    return false;
                }

                errMsg = "";
                FileTransferSession session = TransferSessions[transferId];

                lock (session.FileStream)
                {
                    session.FileStream.Seek(chunkStart, SeekOrigin.Begin);
                    session.FileStream.Write(chunk, 0, chunk.Length);
                    session.FileStream.Flush();
                    session.LastActivityTime = DateTime.Now;
                }

                return true;
            }
            catch (Exception ex)
            {
                errMsg = ex.Message;
                return false;
            }
        }

        /// <summary>
        /// 上传大文件结束
        /// </summary>
        /// <param name="transferId ">上传文件Id</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>是否成功</returns>
        public bool EndUploadLargeFiles(string transferId, bool success, out string fileFingerprint, out string errMsg)
        {
            lock (_lock) 
            {
                try
                {
                    if (TransferSessions.ContainsKey(transferId) == false)
                    {
                        fileFingerprint = "";
                        errMsg = "无效的上传文件Id：" + transferId;
                        return false;
                    }

                    errMsg = "";
                    FileTransferSession session = TransferSessions[transferId];
                    session.FileStream.Close();
                    TransferSessions.Remove(transferId);

                    // 如果不成功
                    if (success == false)
                    {
                        fileFingerprint = "";
                        File.Delete(session.FilePath);
                        return true;
                    }

                    fileFingerprint = GenerateFileFingerprint(session.FileName);

                    IniFileManager iniFileManager = new IniFileManager(getLocalServerIniFilePath());
                    iniFileManager.iniWriteValue(v_file_fingerprint_key, session.FileName, fileFingerprint);

                    if (session.FileName == ClientFileManager.DishesImageFileName)
                    {
                        handleDishesImageFileUpload();
                    }

                    return true;
                }
                catch (Exception ex)
                {
                    fileFingerprint = "";
                    errMsg = ex.Message;
                    return false;
                }
            }
        }

        #endregion

        #region 下载文件

        /// <summary>
        /// 下载文件
        /// </summary>
        /// <param name="fileName">文件名称</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>文件字节内容</returns>
        public byte[] DownloadFile(string fileName, out string errMsg)
        {
            lock (_lock)
            {
                try
                {
                    // 如果当前文件在上传或下载的队列里
                    if (TransferSessions.Values.Any(session => session.FileName == fileName))
                    {
                        // 判断文件是否正在上传
                        FileTransferSession transferSession = TransferSessions.Values.FirstOrDefault((session) => session.FileName == fileName);
                        if (transferSession.IsUpload == true)
                        {
                            errMsg = fileName + " 文件正在进行上传";
                            return null;
                        }

                        // 将 session 的 FileStream 全部读取到字节数组中，并返回
                        lock (transferSession.FileStream)
                        {
                            transferSession.FileStream.Seek(0, SeekOrigin.Begin); // 将流位置设置到开始
                            transferSession.LastActivityTime = DateTime.Now;
                            using (var memoryStream = new MemoryStream())
                            {
                                transferSession.FileStream.CopyTo(memoryStream); // 将文件流复制到内存流
                                errMsg = "";
                                return memoryStream.ToArray(); // 将内存流转换为字节数组并返回
                            }
                        }
                    }

                    errMsg = "";
                    string filePath = Path.Combine(fileBaseDirectory, fileName);

                    if (File.Exists(filePath) == false)
                    {
                        throw new FileNotFoundException("File not found：" + fileName, fileName);
                    }

                    Console.WriteLine(string.Format("下载文件：{0}", fileName));

                    return File.ReadAllBytes(filePath);
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    Console.WriteLine("下载文件报错");
                    return null;
                }
            }
        }

        /// <summary>
        /// 下载品项图片文件
        /// </summary>
        /// <returns></returns>
        public Stream DownloadDishesImageFile()
        {
            lock (_lock)
            {
                // 如果当前文件在上传或下载的队列里
                if (TransferSessions.Values.Any(session => session.FileName == ClientFileManager.DishesImageFileName))
                {
                    // 判断文件是否正在上传
                    FileTransferSession transferSession = TransferSessions.Values.FirstOrDefault((session) => session.FileName == ClientFileManager.DishesImageFileName);
                    if (transferSession.IsUpload == true)
                    {
                        throw new InvalidOperationException(ClientFileManager.DishesImageFileName + " 文件正在进行上传");
                    }

                    // 将 session 的 FileStream 全部读取到字节数组中，并返回
                    lock (transferSession.FileStream)
                    {
                        transferSession.FileStream.Seek(0, SeekOrigin.Begin); // 将流位置设置到开始
                        transferSession.LastActivityTime = DateTime.Now;
                        MemoryStream memoryStream = new MemoryStream();
                        transferSession.FileStream.CopyTo(memoryStream);
                        return memoryStream;
                    }
                }

                string filePath = Path.Combine(fileBaseDirectory, ClientFileManager.DishesImageFileName);
                if (!File.Exists(filePath))
                {
                    throw new FileNotFoundException("File not found：" + ClientFileManager.DishesImageFileName, ClientFileManager.DishesImageFileName);
                }

                return new FileStream(filePath, FileMode.Open, FileAccess.Read);
            }
        }

        /// <summary>
        /// 开始下载大文件
        /// </summary>
        /// <param name="fileName">文件名称</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>下载文件的Id</returns>
        public string BeginDownloadLargeFile(string fileName, out long fileLength, out string errMsg)
        {
            lock (_lock)
            {
                try
                {
                    FileTransferSession transferSession = null;

                    // 如果当前文件在上传或下载的队列里
                    if (TransferSessions.Values.Any(session => session.FileName == fileName))
                    {
                        // 判断文件是否正在上传
                        transferSession = TransferSessions.Values.FirstOrDefault((session) => session.FileName == fileName);
                        if (transferSession.IsUpload == true)
                        {
                            errMsg = fileName + " 文件正在进行上传";
                            fileLength = 0;
                            return "";
                        }

                        errMsg = "";
                        transferSession.LastActivityTime = DateTime.Now;
                        fileLength = transferSession.FileStream.Length;
                        return transferSession.TransferId;
                    }

                    var filePath = Path.Combine(fileBaseDirectory, fileName);

                    if (!File.Exists(filePath))
                    {
                        errMsg = "未找到 " + filePath + " 文件";
                        fileLength = 0;
                        return "";
                    }

                    errMsg = "";
                    var transferId = Guid.NewGuid().ToString();
                    transferSession = new FileTransferSession
                    {
                        FileName = fileName,
                        TransferId = transferId,
                        IsUpload = false,
                        FilePath = filePath,
                        LastActivityTime = DateTime.Now,
                        FileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read, FileShare.Read)
                    };

                    TransferSessions.Add(transferId, transferSession);

                    fileLength = transferSession.FileStream.Length;

                    return transferId;
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    fileLength = 0;
                    return "";
                }
            }
        }

        /// <summary>
        /// 下载文件分片
        /// </summary>
        /// <param name="transferId">下载文件Id</param>
        /// <param name="chunkNumber">分片批次</param>
        /// <param name="chunkSize">分片大小</param>
        /// <param name="errMsg">错误信息</param>
        /// <returns>分片字节</returns>
        public byte[] DownloadLargeFileChunk(string transferId, int chunkStart, int chunkSize, out string errMsg)
        {
            try
            {
                if (TransferSessions.ContainsKey(transferId) == false)
                {
                    errMsg = "无效的下载文件Id：" + transferId;
                    return null;
                }

                errMsg = "";
                FileTransferSession session = TransferSessions[transferId];
                byte[] buffer = null;
                session.LastActivityTime = DateTime.Now;
                lock (session.FileStream)
                {
                    session.FileStream.Seek(chunkStart, SeekOrigin.Begin);

                    if (chunkStart + chunkSize > session.FileStream.Length)
                    {
                        chunkSize = (int)(session.FileStream.Length - chunkStart);
                    }

                    buffer = new byte[chunkSize];

                    var bytesRead = session.FileStream.Read(buffer, 0, chunkSize);
                }

                return buffer;
            }
            catch (Exception ex)
            {
                errMsg = ex.Message;
                return null;
            }
        }

        /// <summary>
        /// 下载文件分片结束
        /// </summary>
        /// <param name="transferId"></param>
        /// <param name="errMsg"></param>
        /// <returns></returns>
        public bool EndDownloadLargeFile(string transferId, out string errMsg)
        {
            lock (_lock) 
            {
                try
                {
                    if (TransferSessions.ContainsKey(transferId) == false)
                    {
                        errMsg = "无效的下载文件Id：" + transferId;
                        return false;
                    }

                    errMsg = "";
                    FileTransferSession transferSession = TransferSessions[transferId];

                    transferSession.FileStream.Close();
                    TransferSessions.Remove(transferId);

                    return true;
                }
                catch (Exception ex)
                {
                    errMsg = ex.Message;
                    return false;
                }
            }
        }
        #endregion

        #region 其他文件相关处理

        /// <summary>
        /// 增加上传品项图片的处理
        /// </summary>
        private void handleDishesImageFileUpload()
        {
            // 获取图片文件夹路径
            string imageFileDirectory = Path.Combine(fileBaseDirectory, ClientFileManager.DishesImageFileDirectory);

            // 删除之前的图片文件夹
            if (Directory.Exists(imageFileDirectory) == true)
            {
                Directory.Delete(imageFileDirectory, true);
            }

            // 创建空的文件夹，以使上传云端图片能够正常
            Directory.CreateDirectory(imageFileDirectory);

            // 如果图片文件存在，则解压文件
            string imageFilePath = Path.Combine(fileBaseDirectory, ClientFileManager.DishesImageFileName);
            if (File.Exists(imageFilePath) == true)
            {
                SharpZipHelper.CustomUnZip(imageFilePath, imageFileDirectory, "");
            }
        }

        #endregion
    }
}

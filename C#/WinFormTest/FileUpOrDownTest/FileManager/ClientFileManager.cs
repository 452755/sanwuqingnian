using FileUpOrDownTest.Common;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileUpOrDownTest.FileManager
{
    internal class ClientFileManager
    {
        private static readonly object _lock = new object();

        public const string DishesImageFileName = "dishesImg.zip";
        public const string DishesImageFileDirectory = "dishesImg";
        private const string v_file_fingerprint_key = "FileFingerprint";

        // 定义每次上传的大小
        private const int chunkSize = 1024 * 1024; // 1MB 分片大小

        private readonly string fileBaseDirectory = "";

        #region Instance

        private static ClientFileManager instance = new ClientFileManager();

        public static ClientFileManager Instance
        {
            get { return instance; }
        }

        private ClientFileManager()
        {
            fileBaseDirectory = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "ClientFiles");
            if (Directory.Exists(fileBaseDirectory) == false)
            {
                Directory.CreateDirectory(fileBaseDirectory);
            }
        }
        #endregion

        /// <summary>
        /// 上传品项图片文件至监控服务主机
        /// </summary>
        public void UploadDishesImageFile(string imageDirectoryPath)
        {
            Thread thread = new Thread(new ParameterizedThreadStart(UploadDishesImageFileThread));
            thread.Start(imageDirectoryPath);
        }

        private void UploadDishesImageFileThread(object threadObject)
        {
            lock (_lock)
            {
                try
                {
                    if (threadObject == null)
                    {
                        return;
                    }

                    string imageDirectoryPath = threadObject.ToString();

                    // 判断之前是否存在压缩文件，存在则删除
                    string zipFilePath = Path.Combine(fileBaseDirectory, DishesImageFileName);
                    if (File.Exists(zipFilePath))
                    {
                        File.Delete(zipFilePath);
                    }

                    string errMsg = "";
                    string fileFingerprint = "";

                    // 如果路径为空，或指定的文件夹不存在，则证明是删除文件
                    if (string.IsNullOrEmpty(imageDirectoryPath) == true || Directory.Exists(imageDirectoryPath) == false)
                    {
                        fileFingerprint = ServiceFileManager.Instance.UploadFile(DishesImageFileName, null, out errMsg);

                        if (string.IsNullOrEmpty(errMsg) == false)
                        {
                            Console.WriteLine("上传品项图片错误：" + errMsg);

                            return;
                        }

                        saveFileFingerprint(fileFingerprint);

                        return;
                    }

                    // 先压缩文件夹
                    SharpZipHelper.ZipDirectory(imageDirectoryPath, fileBaseDirectory, DishesImageFileName.TrimEnd(".zip".ToCharArray()), false);

                    // 创建文件流，并上传文件
                    using (FileStream fs = new FileStream(zipFilePath, FileMode.Open, FileAccess.Read, FileShare.Read))
                    {
                        // 调用开始上传接口
                        string uploadId = ServiceFileManager.Instance.BeginUploadLargeFiles(DishesImageFileName, out errMsg);

                        if (!string.IsNullOrEmpty(errMsg))
                        {
                            Console.WriteLine("开始上传品项图片文件错误：" + errMsg);
                            return;
                        }

                        try
                        {
                            // 定义是否成功变量
                            bool success = false;

                            // 定义分片
                            byte[] buffer = null;
                            // 定义分片开始位置
                            int chunkStart = 0;
                            // 定义分片次数
                            int chunkNum = 1;
                            // 定义最大重试次数
                            const int maxRetries = 3;

                            do
                            {
                                // 如果分片开始加上分片长度，大于文件长度，则将分片长度修改为剩余的长度
                                if (chunkStart + chunkSize > fs.Length)
                                {
                                    buffer = new byte[fs.Length - chunkStart];
                                }
                                else
                                {
                                    buffer = new byte[chunkSize];
                                }

                                // 读取文件分片
                                fs.Read(buffer, 0, buffer.Length);

                                // 定义已经重试次数
                                int retryCount = 0;
                                do
                                {
                                    // 上传分片内容
                                    success = ServiceFileManager.Instance.UploadLargeFileChunk(uploadId, chunkStart, buffer, out errMsg);
                                    if (success == false || string.IsNullOrEmpty(errMsg) == false)
                                    {
                                        Console.WriteLine($"上传分片 {chunkNum} 错误：{errMsg}, 尝试重试 {retryCount}/{maxRetries}");
                                        retryCount++;
                                    }

                                } while (success == false && retryCount <= maxRetries);

                                // 如果重试次数结束以后，还没上传成功
                                if (success == false)
                                {
                                    Console.WriteLine($"上传分片 {chunkNum} 失败：{errMsg}，取消下载");

                                    // 调用上传结束
                                    success = ServiceFileManager.Instance.EndUploadLargeFiles(uploadId, false, out fileFingerprint, out errMsg);
                                    if (success == false || string.IsNullOrEmpty(errMsg) == false)
                                    {
                                        Console.WriteLine("结束上传品项图片文件错误：" + errMsg);
                                    }

                                    return;
                                }

                                // 修改分片开始位置
                                chunkStart += chunkSize;
                                // 自增上传次数
                                chunkNum++;

                                // 如果分片开始位置大于或等于文件长度
                                if (chunkStart >= fs.Length)
                                {
                                    break;
                                }
                            } while (true);

                            // 调用结束上传文件接口
                            success = ServiceFileManager.Instance.EndUploadLargeFiles(uploadId, true, out fileFingerprint, out errMsg);

                            
                            if (success == false || string.IsNullOrEmpty(errMsg) == false)
                            {
                                Console.WriteLine("结束上传品项图片文件错误：" + errMsg);
                                return;
                            }

                            saveFileFingerprint(fileFingerprint);

                            Console.WriteLine("品项图片文件上传成功");
                        }
                        catch (Exception ex)
                        {
                            Console.WriteLine("上传品项图片文件错误：" + ex.Message);

                            // 调用上传结束
                            bool success = ServiceFileManager.Instance.EndUploadLargeFiles(uploadId, false, out fileFingerprint, out errMsg);
                            if (success == false || string.IsNullOrEmpty(errMsg) == false)
                            {
                                Console.WriteLine("结束上传品项图片文件错误：" + errMsg);
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine("上传品项图片文件错误：" + ex);
                }
            }
        }

        /// <summary>
        /// 检查品项图片是否需要更新
        /// </summary>
        public void checkDishesImageFileNeedDownload() 
        {
            Thread thread = new Thread(checkDishesImageFileNeedDownloadThread);
            thread.Start();
        }

        private void checkDishesImageFileNeedDownloadThread() 
        {
            lock (_lock)
            {
                try
                {
                    // 先获取文件指纹
                    string errMsg = "";
                    string fileFingerprint = ServiceFileManager.Instance.GetFileFingerprint(DishesImageFileName, out errMsg);

                    if (string.IsNullOrEmpty(errMsg) == false)
                    {
                        Console.WriteLine("获取品项图片文件指纹错误：" + errMsg);
                        return;
                    }

                    // 获取本地文件指纹
                    string localFileFingerprint = getLocalFileFingerprint();
                    // 判断文件指纹是否一致
                    if (localFileFingerprint == fileFingerprint)
                    {
                        Console.WriteLine("品项图片的服务端文件指纹和本地文件指纹一致");
                        return;
                    }

                    // 删除本地之前的文件夹
                    string imgFilePath = Path.Combine(fileBaseDirectory, DishesImageFileName);
                    if (File.Exists(imgFilePath) == true)
                    {
                        File.Delete(imgFilePath);
                    }

                    // 删除本地之前的文件夹
                    string imgFileDirector = Path.Combine(fileBaseDirectory, DishesImageFileDirectory);
                    if (Directory.Exists(imgFileDirector) == true)
                    {
                        Directory.Delete(imgFileDirector, true);
                    }

                    // 判断获取到的文件指纹是否是要删除文件，因为前置已经删除本地文件，所以直接保存文件指纹然后返回即可
                    string fileFingerprintDecode = CEncoder.Decode(fileFingerprint);
                    if (fileFingerprintDecode.EndsWith("-delete"))
                    {
                        saveFileFingerprint(fileFingerprint);
                        return;
                    }

                    long fileLength = 0;
                    string downloadId = ServiceFileManager.Instance.BeginDownloadLargeFile(DishesImageFileName, out fileLength, out errMsg);

                    if (string.IsNullOrEmpty(errMsg) == false)
                    {
                        Console.WriteLine("开始下载品项图片错误：" + errMsg);
                        return;
                    }

                    bool success = false;

                    // 使用创建文件流
                    using (FileStream stream = new FileStream(imgFilePath, FileMode.Create, FileAccess.Write))
                    {
                        // 定义分片
                        byte[] buffer = null;
                        // 定义分片开始位置
                        int chunkStart = 0;
                        // 定义分片次数
                        int chunkNum = 1;
                        // 定义最大重试次数
                        const int maxRetries = 3;

                        // 定义本次下载的长度
                        int thisChunkSize = chunkSize;

                        do
                        {
                            // 如果分片开始加上分片长度，大于文件长度，则将分片长度修改为剩余的长度
                            if (chunkStart + chunkSize > fileLength)
                            {
                                thisChunkSize = (int)(fileLength - chunkStart);
                            }
                            else
                            {
                                thisChunkSize = chunkSize;
                            }

                            // 定义已经重试次数
                            int retryCount = 0;
                            do
                            {
                                buffer = ServiceFileManager.Instance.DownloadLargeFileChunk(downloadId, chunkStart, thisChunkSize, out errMsg);

                                if (string.IsNullOrEmpty(errMsg) == false)
                                {
                                    success = false;
                                    Console.WriteLine($"下载分片 {chunkNum} 错误：{errMsg}, 尝试重试 {retryCount}/{maxRetries}");
                                    retryCount++;
                                }
                                else
                                {
                                    success = true;
                                }
                            } while (success == false && retryCount <= maxRetries);

                            // 如果重试次数结束以后，还没上传成功
                            if (success == false)
                            {
                                Console.WriteLine($"下载分片 {chunkNum} 失败：{errMsg}，取消下载");

                                success = false;
                                break;
                            }

                            stream.Write(buffer, 0, buffer.Length);
                            stream.Flush();

                            // 修改分片开始位置
                            chunkStart += chunkSize;
                            // 自增上传次数
                            chunkNum++;

                            // 如果分片开始位置大于或等于文件长度
                            if (chunkStart >= fileLength)
                            {
                                break;
                            }
                        } while (true);
                    }

                    // 如果下载不成功，则删除已下载的内容
                    if (success == false)
                    {
                        File.Delete(imgFilePath);
                    }

                    success = ServiceFileManager.Instance.EndDownloadLargeFile(downloadId, out errMsg);
                    if (success == false || string.IsNullOrEmpty(errMsg) == false)
                    {
                        Console.WriteLine("结束下载品项图片错误：" + errMsg);

                        File.Delete(imgFilePath);

                        return;
                    }

                    // 如果文件存在的情况下
                    if (File.Exists(imgFilePath) == true)
                    {
                        // 创建文件夹以供前台展示图片使用
                        Directory.CreateDirectory(imgFileDirector);
                        // 解压文件
                        SharpZipHelper.CustomUnZip(imgFilePath, imgFileDirector, "");

                        // 保存新的文件指纹
                        saveFileFingerprint(fileFingerprint);

						Console.WriteLine("品项图片文件下载成功");
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine("检查品项图片是否更新错误：" + ex.Message);
                }
            }
        }

        private void saveFileFingerprint(string fileFingerprint) 
        {
            string iniFilePath = Path.Combine(fileBaseDirectory, "fileFingerprint.ini");
            IniFileManager iniFileManager = new IniFileManager(iniFilePath);
            iniFileManager.iniWriteValue(v_file_fingerprint_key, DishesImageFileName, fileFingerprint);
        }

        private string getLocalFileFingerprint() 
        {
            string iniFilePath = Path.Combine(fileBaseDirectory, "fileFingerprint.ini");
            IniFileManager iniFileManager = new IniFileManager(iniFilePath);
            return iniFileManager.iniReadValue(v_file_fingerprint_key, DishesImageFileName);
        }
    }
}

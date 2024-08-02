using ICSharpCode.SharpZipLib.Checksum;
using ICSharpCode.SharpZipLib.Zip;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using System;
using System.Collections.Generic;
using System.IO;

namespace FileUpOrDownTest.Common 
{
    public class SharpZipHelper
    {
        private const string m_sZipCompressionPwd = "Abraham";

        public static void ZipFile(string FileToZip, string ZipedPath, string ZipedFileName = "", int CompressionLevel = 5, int BlockSize = 2048, bool IsEncrypt = true)
        {
            if (!File.Exists(FileToZip))
            {
                throw new FileNotFoundException("指定要压缩的文件: " + FileToZip + " 不存在!");
            }

            using (FileStream fileStream = File.OpenWrite(ZipedPath))
            {
                ZipOutputStream val = (ZipOutputStream)(object)new ZipOutputStream((Stream)fileStream);
                try
                {
                    using (FileStream fileStream2 = new FileStream(FileToZip, FileMode.Open, FileAccess.Read))
                    {
                        string text = FileToZip.Substring(FileToZip.LastIndexOf("\\") + 1);
                        ZipEntry val2 = (ZipEntry)(object)new ZipEntry(text);
                        if (IsEncrypt)
                        {
                            val.Password = m_sZipCompressionPwd;
                        }

                        val.PutNextEntry(val2);
                        val.SetLevel(CompressionLevel);
                        byte[] array = new byte[BlockSize];
                        int num = 0;
                        try
                        {
                            do
                            {
                                num = fileStream2.Read(array, 0, array.Length);
                                ((Stream)(object)val).Write(array, 0, num);
                            }
                            while (num > 0);
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }

                        fileStream2.Close();
                    }

                    ((DeflaterOutputStream)val).Finish();
                    ((Stream)(object)val).Close();
                }
                finally
                {
                    ((IDisposable)val)?.Dispose();
                }

                fileStream.Close();
            }
        }

        public static void CustomZipFile(string FileToZip, string ZipedPath, string ZipPassword)
        {
            int level = 5;
            int num = 2048;
            if (!File.Exists(FileToZip))
            {
                throw new FileNotFoundException("指定要压缩的文件: " + FileToZip + " 不存在!");
            }

            using (FileStream fileStream = File.OpenWrite(ZipedPath))
            {
                ZipOutputStream val = (ZipOutputStream)(object)new ZipOutputStream((Stream)fileStream);
                try
                {
                    using (FileStream fileStream2 = new FileStream(FileToZip, FileMode.Open, FileAccess.Read))
                    {
                        string text = FileToZip.Substring(FileToZip.LastIndexOf("\\") + 1);
                        ZipEntry val2 = (ZipEntry)(object)new ZipEntry(text);
                        if (!string.IsNullOrEmpty(ZipPassword))
                        {
                            val.Password = ZipPassword;
                        }

                        val.PutNextEntry(val2);
                        val.SetLevel(level);
                        byte[] array = new byte[num];
                        int num2 = 0;
                        try
                        {
                            do
                            {
                                num2 = fileStream2.Read(array, 0, array.Length);
                                ((Stream)(object)val).Write(array, 0, num2);
                            }
                            while (num2 > 0);
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }

                        fileStream2.Close();
                    }

                    ((DeflaterOutputStream)val).Finish();
                    ((Stream)(object)val).Close();
                }
                finally
                {
                    ((IDisposable)val)?.Dispose();
                }

                fileStream.Close();
            }
        }

        public static void ZipDirectory(string DirectoryToZip, string ZipedPath, string ZipedFileName = "", bool IsEncrypt = true)
        {
            if (!Directory.Exists(DirectoryToZip))
            {
                throw new FileNotFoundException("指定的目录: " + DirectoryToZip + " 不存在!");
            }

            string path = string.IsNullOrEmpty(ZipedFileName) ? (ZipedPath + "\\" + new DirectoryInfo(DirectoryToZip).Name + ".zip") : (ZipedPath + "\\" + ZipedFileName + ".zip");
            using (FileStream fileStream = File.Create(path))
            {
                ZipOutputStream val = (ZipOutputStream)(object)new ZipOutputStream((Stream)fileStream);
                try
                {
                    if (IsEncrypt)
                    {
                        val.Password = m_sZipCompressionPwd;
                    }

                    ZipSetp(DirectoryToZip, val, "");
                }
                finally
                {
                    ((IDisposable)val)?.Dispose();
                }
            }
        }

        public static void ZipFileList(List<string> filelist, string ZipedPath, string ZipedFileName = "", bool IsEncrypt = true)
        {
            using (FileStream fileStream = File.Create(ZipedPath))
            {
                ZipOutputStream val = (ZipOutputStream)(object)new ZipOutputStream((Stream)fileStream);
                try
                {
                    if (IsEncrypt)
                    {
                        val.Password = m_sZipCompressionPwd;
                    }

                    zipSetpFile(filelist, val, "");
                }
                finally
                {
                    ((IDisposable)val)?.Dispose();
                }
            }
        }

        private static void ZipSetp(string strDirectory, ZipOutputStream s, string parentPath)
        {
            if (strDirectory[strDirectory.Length - 1] != Path.DirectorySeparatorChar)
            {
                strDirectory += Path.DirectorySeparatorChar;
            }

            Crc32 val = (Crc32)(object)new Crc32();
            string[] fileSystemEntries = Directory.GetFileSystemEntries(strDirectory);
            string[] array = fileSystemEntries;
            foreach (string text in array)
            {
                if (Directory.Exists(text))
                {
                    string str = parentPath;
                    str += text.Substring(text.LastIndexOf("\\") + 1);
                    str += "\\";
                    ZipSetp(text, s, str);
                    continue;
                }

                using (FileStream fileStream = File.OpenRead(text))
                {
                    byte[] array2 = new byte[fileStream.Length];
                    fileStream.Read(array2, 0, array2.Length);
                    string text2 = parentPath + text.Substring(text.LastIndexOf("\\") + 1);
                    ZipEntry val2 = (ZipEntry)(object)new ZipEntry(text2);
                    val2.DateTime = DateTime.Now;
                    val2.Size = fileStream.Length;
                    fileStream.Close();
                    val.Reset();
                    val.Update(array2);
                    val2.Crc = val.Value;
                    s.PutNextEntry(val2);
                    ((Stream)(object)s).Write(array2, 0, array2.Length);
                }
            }
        }

        private static void zipSetpFile(List<string> files, ZipOutputStream s, string parentPath)
        {
            Crc32 val = (Crc32)(object)new Crc32();
            foreach (string file in files)
            {
                using (FileStream fileStream = File.OpenRead(file))
                {
                    byte[] array = new byte[fileStream.Length];
                    fileStream.Read(array, 0, array.Length);
                    string text = parentPath + file.Substring(file.LastIndexOf("\\") + 1);
                    ZipEntry val2 = (ZipEntry)(object)new ZipEntry(text);
                    val2.DateTime = DateTime.Now;
                    val2.Size = fileStream.Length;
                    fileStream.Close();
                    val.Reset();
                    val.Update(array);
                    val2.Crc = val.Value;
                    s.PutNextEntry(val2);
                    ((Stream)(object)s).Write(array, 0, array.Length);
                }
            }
        }

        public static void UnZip(string ZipFile, string TargetDirectory, bool OverWrite = true)
        {
            //IL_004c: Unknown result type (might be due to invalid IL or missing references)
            //IL_0052: Expected O, but got Unknown
            if (!Directory.Exists(TargetDirectory))
            {
                throw new FileNotFoundException("指定的目录: " + TargetDirectory + " 不存在!");
            }

            if (!TargetDirectory.EndsWith("\\"))
            {
                TargetDirectory += "\\";
            }

            ZipInputStream val = (ZipInputStream)(object)new ZipInputStream((Stream)File.OpenRead(ZipFile));
            try
            {
                val.Password = m_sZipCompressionPwd;
                ZipEntry nextEntry;
                while ((nextEntry = val.GetNextEntry()) != null)
                {
                    string str = "";
                    string text = "";
                    text = nextEntry.Name;
                    if (text != "")
                    {
                        str = Path.GetDirectoryName(text) + "\\";
                    }

                    string fileName = Path.GetFileName(text);
                    Directory.CreateDirectory(TargetDirectory + str);
                    if (!(fileName != "") || (!(File.Exists(TargetDirectory + str + fileName) && OverWrite) && File.Exists(TargetDirectory + str + fileName)))
                    {
                        continue;
                    }

                    using (FileStream fileStream = File.Create(TargetDirectory + str + fileName))
                    {
                        int num = 2048;
                        byte[] array = new byte[2048];
                        while (true)
                        {
                            num = ((Stream)(object)val).Read(array, 0, array.Length);
                            if (num > 0)
                            {
                                fileStream.Write(array, 0, num);
                                continue;
                            }

                            break;
                        }

                        fileStream.Close();
                    }
                }

                ((Stream)(object)val).Close();
            }
            finally
            {
                ((IDisposable)val)?.Dispose();
            }
        }

        public static void CustomUnZip(string zipFile, string targetDirectory, string compressionPwd, bool overWrite = true)
        {
            //IL_004c: Unknown result type (might be due to invalid IL or missing references)
            //IL_0052: Expected O, but got Unknown
            if (!Directory.Exists(targetDirectory))
            {
                throw new FileNotFoundException("指定的目录: " + targetDirectory + " 不存在!");
            }

            if (!targetDirectory.EndsWith("\\"))
            {
                targetDirectory += "\\";
            }

            ZipInputStream val = (ZipInputStream)(object)new ZipInputStream((Stream)File.OpenRead(zipFile));
            try
            {
                val.Password = compressionPwd;
                ZipEntry nextEntry;
                while ((nextEntry = val.GetNextEntry()) != null)
                {
                    string str = "";
                    string text = "";
                    text = nextEntry.Name;
                    if (text != "")
                    {
                        str = Path.GetDirectoryName(text) + "\\";
                    }

                    string fileName = Path.GetFileName(text);
                    Directory.CreateDirectory(targetDirectory + str);
                    if (!(fileName != "") || (!(File.Exists(targetDirectory + str + fileName) && overWrite) && File.Exists(targetDirectory + str + fileName)))
                    {
                        continue;
                    }

                    using (FileStream fileStream = File.Create(targetDirectory + str + fileName))
                    {
                        int num = 2048;
                        byte[] array = new byte[2048];
                        while (true)
                        {
                            num = ((Stream)(object)val).Read(array, 0, array.Length);
                            if (num > 0)
                            {
                                fileStream.Write(array, 0, num);
                                continue;
                            }

                            break;
                        }

                        fileStream.Close();
                    }
                }

                ((Stream)(object)val).Close();
            }
            finally
            {
                ((IDisposable)val)?.Dispose();
            }
        }
    }
}
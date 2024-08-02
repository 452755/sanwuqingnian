using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace FileUpOrDownTest.Common
{
    internal class CEncoder
    {
        public static string Encode(string str)
        {
            try
            {
                DESCryptoServiceProvider dESCryptoServiceProvider = new DESCryptoServiceProvider();
                dESCryptoServiceProvider.Key = Encoding.ASCII.GetBytes("a3f3bc6d43e7f10d".Substring(0, 8));
                dESCryptoServiceProvider.IV = Encoding.ASCII.GetBytes("a3f3bc6d43e7f10d".Substring(0, 8));
                byte[] bytes = Encoding.Default.GetBytes(str);
                MemoryStream memoryStream = new MemoryStream();
                CryptoStream cryptoStream = new CryptoStream(memoryStream, dESCryptoServiceProvider.CreateEncryptor(), CryptoStreamMode.Write);
                cryptoStream.Write(bytes, 0, bytes.Length);
                cryptoStream.FlushFinalBlock();
                StringBuilder stringBuilder = new StringBuilder();
                byte[] array = memoryStream.ToArray();
                foreach (byte b in array)
                {
                    stringBuilder.AppendFormat("{0:X2}", b);
                }

                memoryStream.Close();
                return stringBuilder.ToString();
            }
            catch
            {
                return string.Empty;
            }
        }

        public static string Decode(string str)
        {
            try
            {
                DESCryptoServiceProvider dESCryptoServiceProvider = new DESCryptoServiceProvider();
                dESCryptoServiceProvider.Key = Encoding.ASCII.GetBytes("a3f3bc6d43e7f10d".Substring(0, 8));
                dESCryptoServiceProvider.IV = Encoding.ASCII.GetBytes("a3f3bc6d43e7f10d".Substring(0, 8));
                byte[] array = new byte[str.Length / 2];
                for (int i = 0; i < str.Length / 2; i++)
                {
                    int num = Convert.ToInt32(str.Substring(i * 2, 2), 16);
                    array[i] = (byte)num;
                }

                MemoryStream memoryStream = new MemoryStream();
                CryptoStream cryptoStream = new CryptoStream(memoryStream, dESCryptoServiceProvider.CreateDecryptor(), CryptoStreamMode.Write);
                cryptoStream.Write(array, 0, array.Length);
                cryptoStream.FlushFinalBlock();
                memoryStream.Close();
                return Encoding.Default.GetString(memoryStream.ToArray());
            }
            catch
            {
                return string.Empty;
            }
        }
    }
}

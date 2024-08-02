using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace FileUpOrDownTest.Common
{
    internal class IniFileManager
    {
        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
        public struct STRINGBUFFER
        {
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)]
            public string szText;
        }

        private string _path;

        public string IniPath
        {
            get
            {
                return _path;
            }
            set
            {
                _path = value;
            }
        }

        [DllImport("kernel32", CharSet = CharSet.Auto)]
        private static extern long WritePrivateProfileString(string section, string key, string val, string filePath);

        [DllImport("kernel32", CharSet = CharSet.Auto)]
        private static extern int GetPrivateProfileString(string section, string key, string def, out STRINGBUFFER retVal, int size, string filePath);

        public IniFileManager(string iniPath)
        {
            _path = iniPath;
            if (!File.Exists(_path))
            {
                CreateIniFile();
            }
        }

        public void iniWriteValue(string sectionName, string Key, string Value)
        {
            WritePrivateProfileString(sectionName, Key, Value, _path);
        }

        public string iniReadValue(string sectionName, string Key)
        {
            STRINGBUFFER retVal;
            int privateProfileString = GetPrivateProfileString(sectionName, Key, null, out retVal, 255, _path);
            string szText = retVal.szText;
            return szText.Trim();
        }

        private void CreateIniFile()
        {
            StreamWriter streamWriter = File.CreateText(_path);
            streamWriter.Write("");
            streamWriter.Flush();
            streamWriter.Close();
        }

        public void deleteValueByKey(string sectionName, string Key)
        {
            WritePrivateProfileString(sectionName, Key, null, _path);
        }
    }
}

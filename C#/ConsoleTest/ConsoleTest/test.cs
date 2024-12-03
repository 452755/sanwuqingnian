using System;
using System.IO;
using System.Xml;

public class Program
{
    static void Main(string[] args) 
    {
        Console.WriteLine("## 后台权限定义:");    
        Console.WriteLine();
        Console.WriteLine("|  功能 | 权限码  |");
        Console.WriteLine("| ------------ | ------------ |");
        HandleXmlFile("D:\\Abrahamguo\\Work\\tzx-database-function\\TZXDatabaseFunction\\TZXDatabaseFunction\\Resource\\background_authority_zf.xml");
        System.Console.WriteLine();

        Console.WriteLine("## 前台权限定义");    
        Console.WriteLine();
        Console.WriteLine("|  功能 | 权限码  |");
        Console.WriteLine("| ------------ | ------------ |");
        HandleXmlFile("D:\\Abrahamguo\\Work\\tzx-database-function\\TZXDatabaseFunction\\TZXDatabaseFunction\\Resource\\front_authority_zf.xml");
        Console.WriteLine();

        Console.WriteLine("**备注**");
        Console.WriteLine("在涉及多页面、多修改情况下，项目管理员可以考虑将项目复制备份。具体操作是，回到项目主页，点击新建项目，勾选“复制已存在项目”。");

        Console.WriteLine("按任意键退出...");
        Console.ReadKey();
    }  

    static void HandleXmlFile(string filePath) 
    {
        if (string.IsNullOrWhiteSpace(filePath)) 
        {
            return;
        }

        string xmlContent = File.ReadAllText(filePath);

        if (string.IsNullOrWhiteSpace(xmlContent)) 
        {
            return;
        }

        XmlDocument xmlDoc = new XmlDocument();
        xmlDoc.LoadXml(xmlContent);

        XmlNodeList nodeList = xmlDoc.SelectNodes("FunctionModules/Modules");

        if (nodeList == null || nodeList.Count == 0) 
        {
            return;
        }

        handleXmlNode(nodeList);
    }

    static void handleXmlNode(XmlNodeList nodeList) {
        if (nodeList == null || nodeList.Count == 0) 
        {
            return;
        }

        foreach (XmlNode node in nodeList) 
        {
            string no = node.Attributes["FunctionNo"].Value;
            string name = node.Attributes["Name"].Value;

            System.Console.WriteLine("| {0} | {1}  |", name, no);

            XmlNodeList childs = node.ChildNodes;

            if (childs == null || childs.Count == 0) 
            {
                continue;
            }

            handleXmlNode(childs);
        }
    }
}
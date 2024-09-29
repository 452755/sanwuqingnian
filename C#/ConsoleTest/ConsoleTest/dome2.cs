using JsonObject;
using System;
using System.Collections;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;

class dome2{

/// <summery>
/// gefdagsf
/// </summery>
    public static void Main(string[] args){
        //MethodInfo

        //var totup = (15,"fds");
        //System.Console.WriteLine(totup);



        //var a = new {b=new {c="fsd"}};

        //var d = a.b.c;






        //jgiejfgjhbniogfabjnknbiufbdfvjafhdud
        //int[] arr1 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        //int[] arr2 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        //int[] arr3 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        //int[] arr4 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        //System.Console.WriteLine(getMemory(arr1));
        //System.Console.WriteLine(getMemory(arr2));
        //System.Console.WriteLine(getMemory(arr3));
        //System.Console.WriteLine(getMemory(arr4));

        //Action<object> a = (obj) =>{ Console.WriteLine(obj); };


        //int A = 0b110;
        //System.Console.WriteLine(A);


        // System.Console.WriteLine(A >> 8);
        // System.Console.WriteLine(A << 31);
        // A = 0x600;
        // System.Console.WriteLine(A);


        //test();

        //testAa<int>(()=>{return 1;});

        //testRegex();

        // testJObject.test();

        testDecode();

        Console.WriteLine("按任意键退出...");
        Console.ReadKey();
    }

    public static void testRegex() 
    {
        string uriTemplate = "app/bills/getBillsInfo/{tableNo}/{ddNo}";
        var pattern = "^" + Regex.Escape(uriTemplate).Replace(@"{", "(?<").Replace(@"}", ">[^/]+)") + "$";
        Console.WriteLine(pattern);


        var s = "^app/bills/getBillsInfo/(?<>[^/]+)/(?<>[^/]+)$";
        string url = "app/bills/getBills/101/fdgfadgfd";

        Console.WriteLine(Regex.IsMatch(url, s));
    }

    public static void testAa<TCallBackResult>(Func<TCallBackResult> callback){
        callback();
    }

    public static void testDecode() 
    {
        Console.WriteLine(Decode("1477AB217F095EF2A3ABA96DE174FE14BC2833C76E080391CE31475AC9DB0640A803D416D92A42508FBCA61A43BDFDE2D69F498500B7D473045B3805BB93AAB40A485D68611E8A0D012EBFF1004DC874158F3CE862C789184A462BFDC1AF95030759185C4094F255A309A1D8EA3A70DBD048B9823EC9E6709585DD647FB31165C79332929BD6CAD27C044FC8999E52DA499F4E1DB8BA99B589CACAC533D0960CB1942B3524B26A2C72C64C04DB04FA41149BF71413B7C6938FB9A10571417FF140E0B7168D3EEA8C78081D26D62D0263F547015862BB3AD66F5B1DC64DF2153A0063C47CABAB9801017CF03164120CC0FA70041E750DB9C3E80053FC92BEFADA0F962CC16DD5B65518BB665872D3137FE30306D9D96CD22DDE614587CF22C332A818EC03B830EE307D755E051D451DA774425A532C4625F5B9EACFB2424BC2222B5106CDB26F0C77BB9328C41851C5E5C0B0B8F5E9A7E122E6AFFE57778F6478BCF9BF4793D5AA6A0F790A7DAB54B989675A09BAEE6F38FC5E38E2DD03DD4706EED9CF03F417983B75E8D96DFCF1E5767F59E1814663AB747CC8AFECA2E10538BC9C5981F8D44D043ED081193D05B1CCAD534E5BBC151BFFD232B97D171E1418F44F79FD520DFD50758193B1811129B62910A2CA95F4AB2049C3BD750887B65C8A471C094CB0DA83E8DE47FC985225B06DEB0C4866CE5FEDDB7B27FB5500D0695F5EAFE8DB88FF938B8FD2E07A9BB8E7F43B19DAEB6D2AA634170677D619298681C65F20115B311C2C5671B5B45EBAEC5212E7A5BE588B0F8E3E656DC48A470C0784DE83DED92E5367CCECD962D1C3FE90E255752938A357C38D3D209F37A6994EDABC46FF99E2E4BA33A91749F9F4392316C0016A48930B80357119E9EB06485C0AEDD5CDD92E75489D6863375E33887F36242C705C17D239F05988BD899EBAA98F7B7F70AE2D0C57C2D65E19533EF5092CC49A2D46876CFEA55305EB47EF7433AF207DCCF02F04896FF4D76488E02B741F145FA21972419821FE623A516D97FD57A800FDAF7F553F7C9A7D93097CB321BF0E438A894E497C5A03CF7AADBEFAF4CB19676D375D29B0F0587C6379E0AAF7935658EEF5BF902DA6A0D345C7A811E6B7C1848A77C68B0BCF6D440E6C6902BB19FEB043042010B7DEE0939F4774088978B53509ACCBDDB6C64AF47452E8B926DDF4E9ACC1B99C37855746F6F4889DF40D0199610AFDE14F64650BD3FFA29D8C312E0A8E3392DDBAC70785C0921B5249438887ADC037FB43C8ED0FFB524ECF620AB1AA5845E47D37CAF24E31526152C91F4E9670B00235A83450C3A58508D89BA9F907BE2B442AEB686FC20F1131E739F925B86631695EC01F9C7A72DE0C18F4E9FD85FED1E4E99580D0AC659F60905A1D3CD5BBFFC86FECF3CA89769D42679C2C9B5CE0F930DB6F43FF19B29C15EDD9DDF0493A49BAA8DF101F47FD53315050582EAA9CCF74CC83C4EDBDB1F0667310C1DD7C9253BAE0F87EEBDE87CF64ECF84C775A055A619BE81B174F49E693C9190366EE3FD34C0658578A3DC58534D92144DA5A092D65441E539AE4E178675140CA443E407221787132BE28D8C252F218243A7849A3FE8CD53D5E835C852FA95D28D20D83C199A65D30C8419EE4BBDBFC4EF711844D56BD803E0EEBB91A0712D52C35592B5BB50376492998CB89E9888DE7C302249ABA3079F64537349D8EFD57CF2547946086AFC05D8B849B33541E12DA643826768E44A184EFD73C128C3319568F0D8260141AF6CFE0CCDBE28920782690D71563A6C472E6C1776265C037C74F9C5D1EC4827F389E962AEAA850658761281CEC682A62EB7C8979435605387E932286EBE73B77CEC54D36AEEC6FB047A1AC91A0973D3130C95752B5687CF9085E3196E238058BDD965F70DE71E657292689A4CA99362EA10DD78DE0D833BC6EF9DA32A2D8DCEADF43507D76833CB71CF923A94E31A28496CB0CD04B1D634BB002F631330149136D828424C7F077CD1B6853B611A75B15569FCB27A5508DC7111C19AF4490CA1B09BA7D91819EF850217BBF72B994D40CB06A49FF032B3F2D7BB720E6966569E212FC90B998645C9FB96779CCE67E4EF4D1E45F8BA22059D0600839C6EB30828ABFF13EA74BAEC67BAFCEB739FDD06DC045529708623725C0F2AB624A436405F510D7DEAD7632535959068D96F1ACCE9FA3D67F8D9A8251AA77CBF30C19AEBEFC74BE7673E48174AA60C5B20534100E8E"));
    }

    public static string Decode(string str)
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

    // public static void s(object o!!){


    // }

    /// <summary>
    /// test 方法
    /// </summary>
    public static void test(){} 

    public static string getMemory(object o) // 获取引用类型的内存地址方法  
        {
            GCHandle h = GCHandle.Alloc(o, GCHandleType.WeakTrackResurrection);
          
            IntPtr addr = GCHandle.ToIntPtr(h);

            return "0x" + addr.ToString("X");


        } 

    // public static int RemoveDuplicates(int[] nums){ 
    //     if(nums.Length==0){
    //         return 0; 
    //     }

    //     // 快慢指针，维护 nums［ø..slow］为结果子数组
    //     int slow=0,fast=0;
    //     // 记录一个元素重复的次数
    //     int count=0;

    //     while(fast < nums.Length){ 
    //         if (nums[fast]!= nums[slow]){ 
    //             slow++;
    //             nums[slow]= nums[fast];
    //         } 
    //         else if (slow < fast && count < 2)
    //         {
    //             //当一个元素重复次数不到2次时，也
    //             slow++;
    //             nums[slow]= nums[fast]; 
    //         }

    //         fast++; 
    //         count++;
            
    //         if (fast < nums.Length && nums[fast]!= nums[fast-1])
    //         { 
    //             //遇到不同的元素
    //             count=0; 
    //         }
    //         Console.WriteLine($"slow={slow},fast={fast},count={count}");
    //     }
    //     foreach (var item in nums)
    //     {
    //         System.Console.Write(item+",");
    //     }
    //     System.Console.WriteLine();
    //     //数组长度为索引＋1
    //     return slow+1; 
    // }

    // class testA{
    //     public void test(){}
    //     public testA(){}
    // }

    // class testB:testA{
    //     public testB():base(){

    //     }

    //     public testB(int a){
    //         base.test();
    //     }
    // }

    // delegate void TestDo<T1,T2>(T1 t1,T2 t2);

    // public static TestDoT1<T1,T2>(param TestDo<T1,T2> aaa){

    // }

    // public static void Test(Action args){

    // }
}

namespace JsonParticiple 
{
    /// <summary>
    /// 分词类型
    /// </summary>
    internal enum ParticipleType 
    {
        ObjectStart = 0,
        ObjectEnd = 1,
        ArrayStart = 2,
        ArrayEnd = 3,
        Separator = 4,
        KeyValueSymbol = 5,
        Text = 6
    }

    /// <summary>
    /// 分词
    /// </summary>
    internal class Participle
    {
        /// <summary>
        /// 分词类型
        /// </summary>
        public ParticipleType Type { get; set; }

        /// <summary>
        /// 分词内容
        /// </summary>
        public string Content { get; set; }

        public Participle() 
        {

        }

        public Participle(string content, ParticipleType type)
        {
            this.Content = content;
            this.Type = type;
        }

        public Participle(char content, ParticipleType type) : this(content.ToString(), type)
        {

        }
    }

    internal class ParticipleCollection : List<Participle>
    {
        public override string ToString()
        {
            return base.ToString();
        }
    }

    internal static class Tokenizer 
    {
        public static List<Participle> GetParticiples(string jsonString) 
        {
            if (string.IsNullOrWhiteSpace(jsonString)) 
            {
                throw new ArgumentNullException(nameof(jsonString));
            }

            List<Participle> participles = new List<Participle>();

            for (int i = 0; i < jsonString.Length; i++)
            {
                if (char.IsWhiteSpace(jsonString[i]))
                {
                    continue;
                }
                else if (jsonString[i] == '{')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.ObjectStart);
                    participles.Add(participle);
                }
                else if (jsonString[i] == '}')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.ObjectEnd);
                    participles.Add(participle);
                }
                else if (jsonString[i] == '[')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.ArrayStart);
                    participles.Add(participle);
                }
                else if (jsonString[i] == ']')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.ArrayEnd);
                    participles.Add(participle);
                }
                else if (jsonString[i] == ':')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.KeyValueSymbol);
                    participles.Add(participle);
                }
                else if (jsonString[i] == ',')
                {
                    Participle participle = new Participle(jsonString[i], ParticipleType.Separator);
                    participles.Add(participle);
                }
                else if (jsonString[i] == '"') 
                {

                }
            }

            return participles;
        }
    }
}

namespace JsonObject 
{
    public enum JObjectType 
    {

    }

    public class JObject
    {
        public string Key 
        {
            get;
            set;
        }

        public JObject this[string key]
        {
            get { return GetValue(key); }
        }

        public JObject GetValue(string key)
        {
            return new JObject();
        }

        public T Cast<T>()
        {
            return default(T);
        }
    }

    public class JArrayObject : JObject 
    {

    }
}

public class JsonRead 
{
    // private

    private class Participle
    {

    }

    public static JObject Read(string json) 
    {
        return new JObject();
    }
}



public class testJObject 
{
    public static void test() 
    {
        JObject jObject = new JObject();
        // object v = jObject["gfsh"] as string;
    }
}
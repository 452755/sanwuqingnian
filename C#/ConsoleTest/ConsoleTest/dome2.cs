using System;

using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Text;

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

        Console.WriteLine(Decode("1477AB217F095EF25059F839D724124B647ADADC3E41AC54738E81D29578CB743CA6B6D9F968773FE4BB0B1980ED64B1C83A228FAF23215F2BE0B27C8521FF770C4D631179A1BD996209A9AB4BDEA401C9BFF7D4A6E947A9478D90FD30F04A7A8C92B0139436A6AA309420E2352ACB9D6DE47942F32DB78CFE7E85FAE8556A130F8FF7AB019163C91CD3C57127420FB33025407778A04D16958972B917C5D5205098B0C0EEB9C12557D346B31E29FC614F3AA005D562542E0B1C73A2BCE14A680DA7D7FBE7DBE6CCAF77DBF2FEF60A31978F30A2C6264EDB9E8BE969B7EDF3ACA15F9CF86EAEA305904EF4B06A053D090171267956D07F8B43D383E6CC5EFFCA77D88FAAC848FF82E0ECEF015C172FCD217B1D7A826283AD77C0B9B8272C14EE51A77BAB86328B75DFC540258C8EBB9B66B678BA7DBA9EC97D3021222EC55B4F9D9945373A657DE622D50454BEA606044AAB0ADE62933F94F4DAF2747B5D56AA4B8DE55F9B62C773ED5E31B164975B0978741A12AA04C9CB1A8DD8F4998B0BA33B2E6D9AD6CDCD7B40F32692E16239C7C09B09B4E0F2ED2BC6AAF3315EBE9AED8B32059928523A5628A7E7005CC3214247F975E535AA96BE72BD6488649DED57D326237662A79025142C22A8D7747E93CFBDE699F1C14B5444A80B3F9579D3259B879D1EAF54E7CB3B5FA86DC7A3AE053F970D4AB60A088CAFCB2EB336DF5EBB35285939E93579AAFB8DCB54AB486EB3596DE3295DCC470E1547ED65C6B51C321CDE67BF02723BD92E4EC7B73BF14839EB685D7A2345567A8084BDC835FEC985F3CE58368573812C2D39312A6F84C4DA2640B8E33F33F4A59A55CABA47887B7B64B63BE8F3345FE411146982DC8DB96EDD1AB22A5367F09169702B865B5E9E9566E498059068734E95D6A0818A33596B559B67DF37E36987425734108FF882CC478F83E363B13EDBE97C23B6D5A3C0CF266C2D23430E093B4F3D7EB73FE8AB7F57A6448EDA59B55638AB1A29F86160D1FB8F2D9EDAA5D28EA47B4A26E21EEB68D99E02AB17645A5E6F87DCE0296F3B20095DAAB3F9928FF11D7F9DE0F43DA56DBA5F10419B13F4D50B312BFF362DE8001DDAFA1FF8EF1BE254CF55BCC55E27025738715F3B273FA010C8AE048F44D59F8884D3D820C2EE684DE0A01C1B31114EE0215B7486E20E288DDBBBB32C74F564BA10551FE60B8A16314188ACEF058886AC8F28CB22BB674AE36F08F905A3F6A36E320D466746B984971B18405DD92DD0E0C01D48ECAC55279FA28ABD7E698D465FD26472A57503FDA3A1F5C2F171C250D44AB5D9A6B5E9B8EDC6C586EE352300A212B2E3987DEED9E44B1356ADDC99A566ECA46619D6092BC06A24E7B93D3DC61A89228CA370211C79A4BAA594C64A38C0EDAECA260938F627E6EE8EA899A0A8B3C13B912B6C82604DAC1F699A8BF8EF017A1675B59FB7FA7AEF6D41B31CA3E80523E478133B2CA0C39ADD253DE3752225FF2C2C2A432E553C8CFAB8D232E4C1F561EFEE9C8E587ED0088E800D606C996F6049CFF5D95BA83579276A1DB8F6BB20F32C2B350B7F166C789C3B3CF01C609A267678F167B01EE5812A5ABDAC478AB928EF081A35C8BB1821910EAE2FFC91DA21F74B9F38190EE3907F47E113C6B4B4E22019F019BDF40F67C7B7032A3A9A6751380D85AD7575FC2519A7B728C116F7213836523DC7B333A1D1EBD4B6F0FFDDC5486F06D796534873B769958D4984CADF3E2F627310B10B1C661E9AC47E045EA3BA47F5D50C124D2213488AAD2F1BD6C843ED35B9ECDB698E012965C429CDDC04C360A1683EAE2BC944BFC6B09AC2F5BE17FF97B9D85D49068EC3359C4F698A2A9D1ACFAF13DBC4867DADCD3AB0C53EB34EC673DD245046B8476497B7BAD11D29591752377837E23F93603C5B0DD5E6A9C08597064BD72EE3502133DD82929B1B605E283CDE6EC87D844DBCDD8B1859A5037FA6E14443CA62D5F2D2DB44C0BA04128860638CA4035ADEED55008E0667F8558B6A66A2687A1B6FCA9EC7FBF3C24E6BA2D7D8AD3D3D01186DF55FF618351813BAE88D3585BCDCD7B4612641CFFDEA1C9B339C72B6D7109886551BA792C00E1205BD5403934B0012AE9FE5CE311E89A263B0C0B9F5B79901CC76FBDACC4866DAC4718C1F157446DAA5D97F184E59C04E9F27C5EC1BAD43A2617724DF1B068178EDB69A862667707976A7FD3B0E092BE62220BF74E172FA4DD23330D2F4"));

        Console.WriteLine("按任意键退出...");
        Console.ReadKey();
    }

    public static void testAa<TCallBackResult>(Func<TCallBackResult> callback){
        callback();
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
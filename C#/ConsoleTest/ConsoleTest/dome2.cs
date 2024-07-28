using System;

using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Text;

class dome2{

/// <summery>
/// gefdagsf
/// </summery>
    public static void Main1(string[] args){
        //MethodInfo

        //var totup = (15,"fds");
        //System.Console.WriteLine(totup);


        
        //var a = new {b=new {c="fsd"}};

        //var d = a.b.c;

        
        
        
        
        
        //jgiejfgjhbniogfabjnknbiufbdfvjafhdud
        int[] arr1 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        int[] arr2 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        int[] arr3 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        int[] arr4 = new int[]{1,1,1,1,2,2,3,3,3,4,5,5};
        System.Console.WriteLine(getMemory(arr1));
        System.Console.WriteLine(getMemory(arr2));
        System.Console.WriteLine(getMemory(arr3));
        System.Console.WriteLine(getMemory(arr4));

        //Action<object> a = (obj) =>{ Console.WriteLine(obj); };


        //int A = 0b110;
        //System.Console.WriteLine(A);


        // System.Console.WriteLine(A >> 8);
        // System.Console.WriteLine(A << 31);
        // A = 0x600;
        // System.Console.WriteLine(A);


        //test();

        //testAa<int>(()=>{return 1;});
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
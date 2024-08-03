using System.Windows.Forms;

/// <summary>
/// 扫雷游戏命名空间
/// </summary>
namespace winmine
{
    /// <summary>
    /// 雷块类，继承于图片框控件
    /// </summary>
    internal class ThunderBlock : PictureBox
    {
        /// <summary>
        /// 雷块位置的X坐标
        /// </summary>
        public int X { get; set; }
        /// <summary>
        /// 雷块位置的Y坐标
        /// </summary>
        public int Y { get; set; }
        /// <summary>
        /// 雷块的右击次数
        /// </summary>
        public int RightIndex { get; set; }
        /// <summary>
        /// 雷块的点击状态
        /// </summary>
        public bool IsClick{set;get;}
        /// <summary>
        /// 雷块的构造函数，初始化雷块的大小和位置，和坐标
        /// </summary>
        /// <param name="x">雷块位置的X坐标</param>
        /// <param name="y">雷块位置的Y坐标</param>
        public ThunderBlock(int x, int y) 
        {
            //设置雷块的大小为30,30
            this.Size = new System.Drawing.Size(30, 30);
            //设置雷块的X坐标为X坐标
            this.X = x;
            //设置雷块的Y坐标为Y坐标
            this.Y = y;
            //设置雷块的点击状态为false
            this.IsClick = false;
            //设置雷块的锚点坐标
            this.Location = new System.Drawing.Point(x * 30, Y * 30);
        }
        public ThunderBlock() { }
    }
}

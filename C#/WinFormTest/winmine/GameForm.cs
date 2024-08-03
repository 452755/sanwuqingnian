using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using System.Media;
// using Sunisoft.IrisSkin;

namespace winmine
{
    public partial class GameForm : Form
    {
        //计时数
        int countTime = 0;
        //确认雷数
        int querenMines = 0;
        //雷区
        int[,] Mines = null;
        //已开出雷块 
        int IsNotMine = 0;
        //纵横数和雷数
        int X = 9, Y = 9, Mine = 10;
        public GameForm()
        {
            //初始化窗体
            InitializeComponent();
            ////开始游戏
            //GameStart();
            ////修改窗体
            //ModifyForm();
            //SkinEngine skin = new SkinEngine();
            //skin.SkinAllForm = true;
            //skin.SkinFile = "ssk皮肤/XPBlue.ssk";
        }
        /// <summary>
        /// 初级游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 初级ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //横为9，纵为9，雷数为10,剩余雷数为雷数
            X = 9; Y = 9; Mine = 10;
            //开始游戏
            GameStart();
            //修改窗体
            ModifyForm();
            MenuItemCheck(sender);
        }
        /// <summary>
        /// 中级游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 中级ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //横为16，纵为16，雷数为40,剩余雷数为雷数
            X = 16; Y = 16; Mine = 40;
            //开始游戏
            GameStart();
            //修改窗体
            ModifyForm();
            MenuItemCheck(sender);
        }
        /// <summary>
        /// 高级游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 高级ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //横为30，纵为16，雷数为99,剩余雷数为雷数
            X = 30; Y = 16; Mine = 99;
            //开始游戏
            GameStart();
            //修改窗体
            ModifyForm();
            MenuItemCheck(sender);
        }
        /// <summary>
        /// 自定义游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 自定义ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Dictionary<string, int> dicCustomize = Customize.GetCustomize();
            if (dicCustomize != null)
            {
                X = dicCustomize["宽"];
                Y = dicCustomize["高"];
                Mine = dicCustomize["雷"];
                //开始游戏
                GameStart();
                //修改窗体
                ModifyForm();
                MenuItemCheck(sender);
            }
        }
        /// <summary>
        /// 游戏教程
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 教程ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new Tutorials().Show();
        }
        /// <summary>
        /// 关于游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 关于ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new GameAboutBox().ShowDialog();
        }
        /// <summary>
        /// 退出游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void 退出ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //退出应用程序
            Application.Exit();
        }
        /// <summary>
        /// 开始按钮按下方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Start_MouseDown(object sender, MouseEventArgs e)
        {
            //修改开始按钮的样式为3D样式
            Start.BorderStyle = BorderStyle.Fixed3D;
        }
        /// <summary>
        /// 开始按钮释放方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Start_MouseUp(object sender, MouseEventArgs e)
        {
            //修改开始按钮的样式为单线边框
            Start.BorderStyle = BorderStyle.FixedSingle;
            //停止计时
            GameTimer.Stop();
            //开始游戏
            GameStart();
        }
        /// <summary>
        /// 游戏开始计时方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void GameTimer_Tick(object sender, EventArgs e)
        {
            //计时数自增
            countTime++;
            //如果计时数小于10
            if (countTime < 10)
            {
                //游戏计时百位的图片显示为计数图片集合的0
                this.GameTimeHundredPlace.Image = NumImgList.Images[0];
                //游戏计时十位的图片显示为计数图片集合的0
                this.GameTimeTenPlace.Image = NumImgList.Images[0];
                //游戏计时个位的图片显示为计数图片集合的计时数
                this.GameTimeOnesPlace.Image = NumImgList.Images[countTime];
            }
            //如果计时数小于100
            else if (countTime < 100)
            {
                //计算十位
                int tenPlace = (int)countTime / 10;
                //计算个位
                int onesPlace = (int)countTime % 10;
                //游戏计时百位的图片显示为计数图片集合的0
                this.GameTimeHundredPlace.Image = NumImgList.Images[0];
                //游戏计时十位的图片显示为计数图片集合的十位数
                this.GameTimeTenPlace.Image = NumImgList.Images[tenPlace];
                //游戏计时个位的图片显示为计数图片集合的个位数
                this.GameTimeOnesPlace.Image = NumImgList.Images[onesPlace];
            }
            //如果计时数小于1000
            else if (countTime < 1000)
            {
                //计算百位
                int hundredPlace = (int)countTime / 100;
                //计算十位
                int tenPlace = (int)((int)countTime / 10) % 10;
                //计算个位
                int onesPlace = (int)countTime % 10;
                //游戏计时百位的图片显示为计数图片集合的百位数
                this.GameTimeHundredPlace.Image = NumImgList.Images[hundredPlace];
                //游戏计时十位的图片显示为计数图片集合的十位数
                this.GameTimeTenPlace.Image = NumImgList.Images[tenPlace];
                //游戏计时个位的图片显示为计数图片集合的个位数
                this.GameTimeOnesPlace.Image = NumImgList.Images[onesPlace];
            }
            //如果计时数大于等于1000
            else
            {
                //游戏计时百位的图片显示为计数图片集合的9
                this.GameTimeHundredPlace.Image = NumImgList.Images[9];
                //游戏计时十位的图片显示为计数图片集合的9
                this.GameTimeTenPlace.Image = NumImgList.Images[9];
                //游戏计时个位的图片显示为计数图片集合的9
                this.GameTimeOnesPlace.Image = NumImgList.Images[9];
            }
        }
        /// <summary>
        /// 改变游戏等级的选中状态
        /// </summary>
        /// <param name="sender"></param>
        private void MenuItemCheck(object sender) 
        {
            ToolStripMenuItem toolStripMenuItem = sender as ToolStripMenuItem;
            初级ToolStripMenuItem.Checked = false;
            中级ToolStripMenuItem.Checked = false;
            高级ToolStripMenuItem.Checked = false;
            自定义ToolStripMenuItem.Checked = false;
            toolStripMenuItem.Checked = true;
        }

        /// <summary>
        /// 游戏开始方法
        /// </summary>
        private void GameStart()
        { 
            //生成雷
            GenerateMines();
            //生成雷块
            GenerateBlock();
            //修改剩余雷数
            UpdateRemainingMines(); 
        }
        /// <summary>
        /// 修改窗体
        /// </summary>
        private void ModifyForm()
        {
            //计算雷区宽度
            int x = 30 * X;
            //计算雷区高度
            int y = 30 * Y;
            //修改雷区的大小
            this.Minefield.Size = new Size(x, y);
            //修改游戏信息区域的宽度
            this.GameInfo.Size = new Size(x, 70);
            //设置游戏开始按钮位置的纵坐标
            int startX = (x / 2) - 25;
            //修改游戏开始按钮的位置
            this.Start.Location = new Point(startX, 10);
            //设置游戏计时位置的纵坐标
            int timeX = x - 75;
            //修改游戏计时的位置
            this.GameTime.Location = new Point(timeX, 10);
            //修改窗体工作区的大小
            this.ClientSize = new System.Drawing.Size(x, y + 80);
        }
        /// <summary>
        /// 生成雷块
        /// </summary>
        private void GenerateBlock()
        {
            this.GameTimeHundredPlace.Image = NumImgList.Images[0];
            this.GameTimeTenPlace.Image = NumImgList.Images[0];
            this.GameTimeOnesPlace.Image = NumImgList.Images[0];
            this.Start.BackgroundImage = StartImgList.Images[0];
            //使计时数归零
            countTime = 0;
            //启用雷区，使雷区可以响应
            this.Minefield.Enabled = true;
            //清除雷区所有的雷块
            this.Minefield.Controls.Clear();
            //循环X轴
            for (int x = 0; x < X; x++)
            {
                //循环Y轴
                for (int y = 0; y < Y; y++)
                {
                    //初始化新的雷块对象
                    ThunderBlock thunderBlock = new ThunderBlock(x, y);
                    //设置雷块的图片为未点击状态的图片，雷块图片集合第14张图片
                    thunderBlock.Image = MineBlockImgList.Images[14];
                    //设置雷块的鼠标按下事件
                    thunderBlock.MouseDown += ThunderBlock_MouseDown;
                    //设置雷块的鼠标抬起方法
                    thunderBlock.MouseUp += ThunderBlock_MouseUp;
                    //设置雷块的鼠标点击事件
                    thunderBlock.MouseClick += ThunderBlock_MouseClick;
                    //设置雷块的鼠标离开事件
                    thunderBlock.MouseLeave += ThunderBlock_MouseLeave;
                    //设置雷块的鼠标移动事件
                    thunderBlock.MouseMove += ThunderBlock_MouseMove;
                    //将雷块对象添加到雷区控件的控件集合中
                    this.Minefield.Controls.Add(thunderBlock);
                }
            }
        }
        /// <summary>
        /// 雷块的鼠标抬起方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ThunderBlock_MouseUp(object sender, MouseEventArgs e)
        {
            //将sender转换成雷块对象
            ThunderBlock thunderBlock = sender as ThunderBlock;
            if (thunderBlock.RightIndex == 3)
            {
                zaicidianji(thunderBlock, false);
            }
            GameIsWin();
        }
        /// <summary>
        /// 雷块的鼠标按下方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ThunderBlock_MouseDown(object sender, MouseEventArgs e)
        {
            //将sender转换成雷块对象
            ThunderBlock thunderBlock = sender as ThunderBlock;
            if (thunderBlock.RightIndex == 3)
            {
                zaicidianji(thunderBlock, true);
            }
        }

        /// <summary>
        /// 鼠标移动到雷块上时发生
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ThunderBlock_MouseMove(object sender, MouseEventArgs e)
        {
            //将sender转换成雷块对象
            ThunderBlock thunderBlock = sender as ThunderBlock;
            //判断雷块没有被点击
            if (!thunderBlock.IsClick)
            {
                //修改雷块的图片为雷块图片集合的第0张，为没有雷是的图片
                thunderBlock.Image = MineBlockImgList.Images[0];
            }
        }
        /// <summary>
        /// 鼠标离开雷块时发生
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ThunderBlock_MouseLeave(object sender, EventArgs e)
        {
            //将sender转换成雷块对象
            ThunderBlock thunderBlock = sender as ThunderBlock;
            //判断雷块没有被点击
            if (!thunderBlock.IsClick)
            {
                //修改雷块的图片为雷块图片集合的第14张，未点击的块状
                thunderBlock.Image = MineBlockImgList.Images[14];
            }
        }

        /// <summary>
        /// 雷块的点击方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ThunderBlock_MouseClick(object sender, MouseEventArgs e)
        {
            if (GameTimer.Enabled==false)
            {
                GameTimer.Start();
            }
            ThunderBlock thunderBlock = sender as ThunderBlock;
            thunderBlock.IsClick = true;
            if (e.Button == MouseButtons.Left)
            {
                if (thunderBlock.RightIndex == 0)
                {
                    if (Mines[thunderBlock.X, thunderBlock.Y] == 0)
                    {
                        thunderBlock.Image = MineBlockImgList.Images[0];
                        thunderBlock.RightIndex = 3;
                        IsNotMine++;
                        List<ThunderBlock> thunderBlocks = new List<ThunderBlock>();
                        thunderBlocks.Add(thunderBlock);
                        pailei(thunderBlocks);
                    }
                    else if (Mines[thunderBlock.X, thunderBlock.Y] == 9)
                    {
                        GameTimer.Stop();
                        ShowMines(thunderBlock);
                    }
                    else
                    {
                        thunderBlock.Image = MineBlockImgList.Images[Mines[thunderBlock.X, thunderBlock.Y]];
                        thunderBlock.RightIndex = 3;
                        IsNotMine++;
                    }
                }
            }
            else
            {
                if (thunderBlock.RightIndex == 0)
                {
                    thunderBlock.RightIndex = 1;
                    thunderBlock.Image = MineBlockImgList.Images[12];
                    querenMines++;
                    UpdateRemainingMines();
                }
                else if (thunderBlock.RightIndex == 1)
                {
                    thunderBlock.RightIndex = 2;
                    thunderBlock.Image = MineBlockImgList.Images[13];
                    //修改确定雷数，确定雷数-1
                    querenMines--;
                    //显示剩余雷数
                    UpdateRemainingMines();
                }
                else if (thunderBlock.RightIndex == 2)
                {
                    //设置雷块的右击次数为0
                    thunderBlock.RightIndex = 0;
                    //设置雷块的图片为雷块图片集合的第14张，未被点击的雷块
                    thunderBlock.Image = MineBlockImgList.Images[14];
                    //设置雷块的点击状态为未点击
                    thunderBlock.IsClick = false;
                }
            }
        }
        /// <summary>
        /// 游戏是否胜利
        /// </summary>
        private void GameIsWin()
        {
            if ((IsNotMine == ((X * Y) - Mine)) || (IsNotMine > ((X * Y) - Mine)))
            {
                GameTimer.Stop();
                this.Start.BackgroundImage = StartImgList.Images[2];
                System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GameForm));

                SoundPlayer sp = new SoundPlayer(((System.IO.Stream)(resources.GetObject("胜利"))));
                sp.Play();
                //SoundPlayer sp = new SoundPlayer(Properties.Resources.胜利);
                //sp.Play();
                MessageBox.Show("you win\n你赢了");
                this.Minefield.Enabled = false;
            }
        }

        /// <summary>
        /// 修改剩余雷数，并显示出来
        /// </summary>
        private void UpdateRemainingMines() 
        {
            //计算剩余雷数，剩余雷数等于总雷数-确认雷数
            int remainingMines = Mine - querenMines;
            if (remainingMines < 0) 
            {
                //剩余雷数百位的图片显示为计数图片集合的0
                this.MinesHundredPlace.Image = NumImgList.Images[0];
                //剩余雷数十位的图片显示为计数图片集合的0
                this.MinesTenPlace.Image = NumImgList.Images[0];
                //剩余雷数个位的图片显示为计数图片集合的0
                this.MinesOnesPlace.Image = NumImgList.Images[0];
            }
            //如果剩余雷数小于10
            else if (remainingMines < 10)
            {
                //剩余雷数百位的图片显示为计数图片集合的0
                this.MinesHundredPlace.Image = NumImgList.Images[0];
                //剩余雷数十位的图片显示为计数图片集合的0
                this.MinesTenPlace.Image = NumImgList.Images[0];
                //剩余雷数个位的图片显示为计数图片集合的剩余雷数
                this.MinesOnesPlace.Image = NumImgList.Images[remainingMines];
            }
            //如果剩余雷数小于100
            else if (remainingMines < 100)
            {
                //计算十位
                int tenPlace = (int)remainingMines / 10;
                //计算个位
                int onesPlace = (int)remainingMines % 10;
                //剩余雷数百位的图片显示为计数图片集合的0
                this.MinesHundredPlace.Image = NumImgList.Images[0];
                //剩余雷数十位的图片显示为计数图片集合的十位数
                this.MinesTenPlace.Image = NumImgList.Images[tenPlace];
                //剩余雷数个位的图片显示为计数图片集合的个位数
                this.MinesOnesPlace.Image = NumImgList.Images[onesPlace];
            }
            //如果剩余雷数大于等于100
            else if(remainingMines > 100 || remainingMines == 100)
            {
                //计算百位
                int hundredPlace = (int)remainingMines / 100;
                //计算十位
                int tenPlace = (int)((int)remainingMines / 10) % 10;
                //计算个位
                int onesPlace = (int)remainingMines % 10;
                //剩余雷数百位的图片显示为计数图片集合的百位数
                this.MinesHundredPlace.Image = NumImgList.Images[hundredPlace];
                //剩余雷数十位的图片显示为计数图片集合的十位数
                this.MinesTenPlace.Image = NumImgList.Images[tenPlace];
                //剩余雷数个位的图片显示为计数图片集合的个位数
                this.MinesOnesPlace.Image = NumImgList.Images[onesPlace];
            }
        }
        /// <summary>
        /// 生成雷
        /// </summary>
        private void GenerateMines() 
        {
            Mines = new int[X, Y];
            IsNotMine = 0;
            querenMines = 0;
            Random ran = new Random();
            for (int i = 0; i < Mine; i++)
            {
                do
                {
                    int x = ran.Next(X);
                    int y = ran.Next(Y);
                    if (Mines[x, y] != 9)
                    {
                        Mines[x, y] = 9;
                        break;
                    }
                } while (true);
            }
            for (int i = 0; i < X; i++)
            {
                for (int j = 0; j < Y; j++)
                {
                    if (Mines[i, j] != 9)
                    {
                        if (i == 0 && j == 0)
                        {
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                        }
                        else if (i == X - 1 && j == 0)
                        {
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                        }
                        else if (i == X - 1 && j == Y - 1)
                        {
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                        }
                        else if (i == 0 && j == Y - 1)
                        {
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                        }
                        else if (i > 0 && i < X - 1 && j == 0)
                        {
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                        }
                        else if (i == X - 1 && j < Y - 1 && j > 0)
                        {
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                        }
                        else if (i > 0 && i < X - 1 && j == Y - 1)
                        {
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                        }
                        else if (i == 0 && j < Y - 1 && j > 0)
                        {
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                        }
                        else
                        {
                            if (Mines[i - 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j - 1] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j] == 9) Mines[i, j] += 1;
                            if (Mines[i + 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j + 1] == 9) Mines[i, j] += 1;
                            if (Mines[i - 1, j] == 9) Mines[i, j] += 1;
                        }
                    }
                }
            }
        }

        /// <summary>
        /// 显示所有雷
        /// </summary>
        private void ShowMines(ThunderBlock thunderBlock) 
        {
            foreach (ThunderBlock item in this.Minefield.Controls)
            {
                if (Mines[item.X, item.Y] != 9 && item.RightIndex == 1)
                {
                    item.Image = MineBlockImgList.Images[11];
                }
                else if (Mines[item.X, item.Y] == 9) 
                {
                    item.Image = MineBlockImgList.Images[9];
                }
            }
            thunderBlock.Image = MineBlockImgList.Images[10];
            this.Start.BackgroundImage = StartImgList.Images[1];
            SoundPlayer sp = new SoundPlayer(Properties.Resources.失败);
            sp.Play();
            MessageBox.Show("you failure\n你失败了");
            this.Minefield.Enabled = false;
        }

        private void 保存记录ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Bitmap bit = new Bitmap(this.Width, this.Height);//实例化一个和窗体一样大的bitmap
            //Graphics g = Graphics.FromImage(bit);
            //g.CompositingQuality = CompositingQuality.HighQuality;//质量设为最高
            //g.CopyFromScreen(this.Left, this.Top, 0, 0, new Size(this.Width, this.Height));//保存整个窗体为图片
            //                                                                               //g.CopyFromScreen(panel游戏区 .PointToScreen(Point.Empty), Point.Empty, panel游戏区.Size);//只保存某个控件（这里是panel游戏区）
           
            //bit.Save("weiboTemp.png");//默认保存格式为PNG，保存成jpg格式质量不是很好
        }

        private void 查看记录ToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 排除周围没有雷的雷块
        /// </summary>
        private void pailei(List<ThunderBlock> thunderBlocks) 
        {
            List<ThunderBlock> newThunderBlocks = new List<ThunderBlock>();
            foreach (ThunderBlock item in thunderBlocks)
            {
                int i = item.X;
                int j = item.Y;
                if (i == 0 && j == 0)
                {
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 9)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i == X - 1 && j == 0)
                {
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i == X - 1 && j == Y - 1)
                {
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i == 0 && j == Y - 1)
                {
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i > 0 && i < X - 1 && j == 0)
                {
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i == X - 1 && j < Y - 1 && j > 0)
                {
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i > 0 && i < X - 1 && j == Y - 1)
                {
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else if (i == 0 && j < Y - 1 && j > 0)
                {
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
                else
                {
                    if (Mines[i - 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j - 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j - 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i + 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i + 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j + 1] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j + 1]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    if (Mines[i - 1, j] == 0)
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[0];
                            newThunderBlocks.Add(thunderBlock);
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                    else
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 0)
                        {
                            thunderBlock.Image = this.MineBlockImgList.Images[Mines[i - 1, j]];
                            thunderBlock.IsClick = true;
                            thunderBlock.RightIndex = 3;
                            IsNotMine++;
                        }
                    }
                }
            }
            if (newThunderBlocks.Count == 0) 
            {
                return;
            }
            else
            {
                pailei(newThunderBlocks);
            }
        }
        /// <summary>
        /// 雷块已经被点击的时再次点击的方法
        /// </summary>
        private void zaicidianji(ThunderBlock newThunderBlock,bool downorup) 
        {
            List<ThunderBlock> newThunderBlocks = new List<ThunderBlock>();
            int i = newThunderBlock.X;
            int j = newThunderBlock.Y;
            int count=0;
            {
                if (i == 0 && j == 0)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i == X - 1 && j == 0)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i == X - 1 && j == Y - 1)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i == 0 && j == Y - 1)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i > 0 && i < X - 1 && j == 0)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i == X - 1 && j < Y - 1 && j > 0)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i > 0 && i < X - 1 && j == Y - 1)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else if (i == 0 && j < Y - 1 && j > 0)
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
                else
                {
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    { 
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                    {
                        ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                        if (thunderBlock.RightIndex == 1)
                        {
                            count++;
                        }
                    }
                }
            }
            if (downorup)
            {
                if (count != Mines[i, j])
                {
                    if (i == 0 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i == X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i == X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i == 0 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i == X - 1 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else if (i == 0 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                    else
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[0];
                            }
                        }
                    }
                } 
            }
            else
            {
                if (count != Mines[i, j])
                {
                    if (i == 0 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i == X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i == X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock; if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i == 0 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i == X - 1 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else if (i == 0 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                    else
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                thunderBlock.Image = MineBlockImgList.Images[14];
                            }
                        }
                    }
                }
                else
                {
                    if (i == 0 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i == X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i == X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i == 0 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i == X - 1 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i > 0 && i < X - 1 && j == Y - 1)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else if (i == 0 && j < Y - 1 && j > 0)
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                    else
                    {
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j - 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j - 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j - 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j - 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j - 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i + 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i + 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i + 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i + 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i + 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point(((i * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), (((j + 1) * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j + 1] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j + 1] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j + 1]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j + 1] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                        {
                            ThunderBlock thunderBlock = this.Minefield.GetChildAtPoint(new Point((((i - 1) * 30) + 5), ((j * 30) + 5))) as ThunderBlock;
                            if (thunderBlock.RightIndex == 0)
                            {
                                if (Mines[i - 1, j] == 9) ShowMines(new ThunderBlock());
                                if (Mines[i - 1, j] != 9)
                                {
                                    thunderBlock.Image = MineBlockImgList.Images[Mines[i - 1, j]];
                                    thunderBlock.IsClick = true;
                                    thunderBlock.RightIndex = 3;
                                    IsNotMine++;
                                }
                                if (Mines[i - 1, j] == 0) newThunderBlocks.Add(thunderBlock);
                            }
                        }
                    }
                }
                if (newThunderBlocks.Count == 0)
                {
                    return;
                }
                else
                {
                    pailei(newThunderBlocks);
                }
            }
            //if (count != Mines[i, j])
            //{

            //}
            //if (!downorup && count == Mines[i, j])
            //{
            //    newThunderBlocks.Add(newThunderBlock);
            //    pailei(newThunderBlocks);
            //}
        }
    }
}

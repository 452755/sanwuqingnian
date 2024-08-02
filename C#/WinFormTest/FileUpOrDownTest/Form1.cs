using FileUpOrDownTest.Common;
using FileUpOrDownTest.FileManager;

namespace FileUpOrDownTest
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void fileUpload_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog fbd = new FolderBrowserDialog();

            if (fbd.ShowDialog() == DialogResult.OK)
            {
                this.textBoxFilePath.Text = fbd.SelectedPath;

                ClientFileManager.Instance.UploadDishesImageFile(this.textBoxFilePath.Text);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            StringRedir r = new StringRedir(ref this.textBox1);
            Console.SetOut(r);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            ServiceFileManager.Instance.UpdateFileFingerprint(ClientFileManager.DishesImageFileName);


        }

        private void fileDownload_Click(object sender, EventArgs e)
        {
            ClientFileManager.Instance.checkDishesImageFileNeedDownload();
        }

        private void 直接下载文件_Click(object sender, EventArgs e)
        {
            ServiceFileManager.Instance.DownloadFile(ClientFileManager.DishesImageFileName, out string errMsg);
            Console.WriteLine(errMsg);
            Console.WriteLine("下载品项图片");
        }
    }
}

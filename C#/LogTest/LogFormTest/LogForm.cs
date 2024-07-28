using System.Text.RegularExpressions;
using System.Text;

namespace RestaurantLocalService
{
    public partial class LogForm : Form
    {
        enum test 
        {
            t = 1,
            c = 2
        }

        private StringBuilder sb = new StringBuilder();

        public LogForm()
        {
            InitializeComponent();
        }

        private void LogForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            base.WindowState = FormWindowState.Normal;
            base.Hide();
            e.Cancel = true;
        }

        /// <summary>
        /// 区分大小写按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonMatchCase_Click(object sender, EventArgs e)
        {
            // test t = (test)Enum.Parse(typeof(test), "t", ignoreCase: true);
        }

        /// <summary>
        /// 全字匹配按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonMatchWholeWord_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 使用正则表达式按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonRegularExpression_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 过滤按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonFilter_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 清除过滤按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonClearFilter_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 切换自动换行按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonWordWrap_Click(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// 全部清除按钮点击
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButtonClearAll_Click(object sender, EventArgs e)
        {

        }
    }
}
using MsCAD.DatabaseServices;
using MsMath;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MsCAD.Windows
{
    public partial class TempDrawForm : Form
    {
        double G_angle1 = 0;
        double G_angle2 = 0;

        public  double SetAngle1 = 0;
        public double SetAngle2 = 0;
        public TempDrawForm(double angle1,double angle2)
        {
            InitializeComponent();
            G_angle1 = angle1;
            G_angle2 = angle2;
        }

        private void TempDrawForm_Load(object sender, EventArgs e)
        {

            
        }

        double GetAngle(double angle)
        {
            double startAngleInCanvas = MathUtils.NormalizeRadianAngle(-angle);
            return (double)(startAngleInCanvas * 180.0 / Utils.PI);
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            Graphics graphics = null;
            try
            {


                graphics = CreateGraphics();
                Vector2 center = new Vector2(290, 230);

                Vector2 start1 = MathUtils.CenterRadiusPoint(center, -G_angle1, 200);
                Vector2 end1 = MathUtils.CenterRadiusPoint(center, -G_angle1 + Math.PI, 200);
                graphics.DrawLine(new Pen(Color.Red), start1.ToPointF(), end1.ToPointF());

                Vector2 start2 = MathUtils.CenterRadiusPoint(center, -G_angle2, 200);
                Vector2 end2 = MathUtils.CenterRadiusPoint(center, -G_angle2 + Math.PI, 200);
                graphics.DrawLine(new Pen(Color.Red), start2.ToPointF(), end2.ToPointF());
                DrawArc(graphics, center, 50, G_angle1, G_angle2);
                DrawArc(graphics, center, 50, G_angle2, G_angle1 + Math.PI);
                DrawArc(graphics, center, 50, G_angle1 + Math.PI, G_angle2 + Math.PI);
                DrawArc(graphics, center, 50, G_angle2 + Math.PI, G_angle1);


                Arc arc1 = new Arc();
                arc1.center = center;
                arc1.radius = 70;
                arc1.startAngle = -G_angle1;
                arc1.endAngle = -G_angle2;
                Vector2 pos1 = MathUtils.GetArcPoint(arc1, 0.5, 1);
                Font font = new Font("黑体", 12, FontStyle.Underline ^ FontStyle.Bold);
                graphics.DrawString("F3", font, new SolidBrush(Color.Blue), pos1.ToPointF());

                Arc arc2 = new Arc();
                arc2.center = center;
                arc2.radius = 70;
                arc2.startAngle = -G_angle2;
                arc2.endAngle = -G_angle1 + Math.PI;
                Vector2 pos2 = MathUtils.GetArcPoint(arc2, 0.5, 1);
                graphics.DrawString("F4", font, new SolidBrush(Color.Blue), pos2.ToPointF());

                Arc arc3 = new Arc();
                arc3.center = center;
                arc3.radius = 70;
                arc3.startAngle = -G_angle1 + Math.PI;
                arc3.endAngle = -G_angle2 + Math.PI;
                Vector2 pos3 = MathUtils.GetArcPoint(arc3, 0.5, 1);

                graphics.DrawString("F1", font, new SolidBrush(Color.Blue), pos3.ToPointF());

                Arc arc4 = new Arc();
                arc4.center = center;
                arc4.radius = 70;
                arc4.startAngle = -G_angle2 + Math.PI;
                arc4.endAngle = -G_angle1;
                Vector2 pos4 = MathUtils.GetArcPoint(arc4, 0.5, 1);

                graphics.DrawString("F2", font, new SolidBrush(Color.Blue), pos4.ToPointF());


                graphics.Save();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            finally
            {
                if (graphics != null)
                    graphics.Dispose();
            }
            this.timer1.Enabled = false;
        }

        /// <summary>
        /// 绘制圆弧,逆时针从startAngle到endAngle
        /// </summary>
        public void DrawArc(Graphics graphics, Vector2 center, double radius, double startAngle, double endAngle)
        {
 

            // GDI为顺时针绘制圆弧,而当前函数为逆时针绘制圆弧
            double startAngleInCanvas = MathUtils.NormalizeRadianAngle(-startAngle);
            double endAngleInCanvas = MathUtils.NormalizeRadianAngle(-endAngle);
            //
            double angle = endAngle - startAngle;
            if (endAngle < startAngle)
            {
                angle += Utils.PI * 2;
            }
            //
            if (radius > 0)
            {
                graphics.DrawArc(new Pen(Color.Green),
                (float)(center.x - radius), (float)(center.y - radius),
                (float)radius * 2, (float)radius * 2,
                (float)(startAngleInCanvas * 180.0 / Utils.PI), -(float)(angle * 180.0 / Utils.PI));
            }
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            try
            {
                int key = (int)keyData;
                if (keyData == Keys.F1)
                {
                    SetAngle1 = G_angle1;
                    SetAngle2 = G_angle2;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F2)
                {
                    SetAngle1 = G_angle2;
                    SetAngle2 = G_angle1 + Math.PI;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F3)
                {
                    SetAngle1 = G_angle1 + Math.PI;
                    SetAngle2 = G_angle2 + Math.PI;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.F4)
                {
                    SetAngle1 = G_angle2 + Math.PI;
                    SetAngle2 = G_angle1;
                    this.DialogResult = DialogResult.OK;
                    return true;
                }
                if (keyData == Keys.Escape || keyData == Keys.Enter || keyData == Keys.Space)
                {
                    this.DialogResult = DialogResult.Cancel;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception ex)
            {
                MainWin.Instance.ShowMsg(TipsState.Error, ex.Message);
                return false;
            }
        }

        
    }
}

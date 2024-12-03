using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace MsCAD
{
    public interface IPresenter
    {
        IDocument document { get; }
        ICanvas canvas { get; }

        void OnPaintCanvas(PaintEventArgs e);
        void OnResize(EventArgs e);
        void OnMouseDown(MouseEventArgs e);
        void OnMouseUp(MouseEventArgs e);
        void OnMouseMove(MouseEventArgs e);
        void OnMouseDoubleClick(MouseEventArgs e);
        void OnMouseWheel(MouseEventArgs e);

        void OnKeyDown(KeyEventArgs e);
        void OnProcessCmdKey(Keys keyData);
        void OnKeyUp(KeyEventArgs e);

        void OnCommand(ICommand cmd);

        double ModelToCanvas(double value);
        MsMath.Vector2 ModelToCanvas(MsMath.Vector2 pointInModel);
        double CanvasToModel(double value);
        MsMath.Vector2 CanvasToModel(MsMath.Vector2 pointInCanvas);
    }
}

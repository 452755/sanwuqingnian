﻿using System;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.Commands.Modify.Offset
{
    internal class OffsetLine : _OffsetOperation
    {
        private Line _line = null;
        private Line _result = null;

        private Vector2 _lineDir = new Vector2(0, 0);
        private Vector2 _lineN1 = new Vector2(0, 0);
        private Vector2 _lineN2 = new Vector2(0, 0);
        private double _crossDirN1 = 0.0;

        public override Entity result
        {
            get { return _result; }
        }

        public OffsetLine(Entity entity)
            : base()
        {
            _line = entity as Line;
            if (_line != null)
            {
                _result = _line.Clone() as Line;
                _lineDir = (_line.endPoint - _line.startPoint).normalized;
                _lineN1 = new Vector2(_lineDir.y, -_lineDir.x);
                _lineN2 = new Vector2(-_lineDir.y, _lineDir.x);
                _crossDirN1 = Vector2.Cross(_lineDir, _lineN1);
            }
        }

        public override bool Do(double value, Vector2 refPoint)
        {
            if (_line == null
                || _result == null)
            {
                return false;
            }

            if (_crossDirN1 * Vector2.Cross(_lineDir, refPoint - _line.startPoint) > 0)
            {
                _result.startPoint = _line.startPoint + _lineN1 * Math.Abs(value);
                _result.endPoint = _line.endPoint + _lineN1 * Math.Abs(value);
            }
            else
            {
                _result.startPoint = _line.startPoint + _lineN2 * Math.Abs(value);
                _result.endPoint = _line.endPoint + _lineN2 * Math.Abs(value);
            }

            return true;
        }
    }
}

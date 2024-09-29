using System;
using System.Collections.Generic;

using MsCAD.DatabaseServices;
using MsMath;

namespace MsCAD.ApplicationServices
{
    public struct Selection
    {
        private ObjectId _objectId;
        public ObjectId objectId
        {
            get { return _objectId; }
            set { _objectId = value; }
        }

        private Vector2 _position;
        public Vector2 position
        {
            get { return _position; }
            set { _position = value; }
        }

        public Selection(ObjectId objectId, Vector2 pickPosition)
        {
            _objectId = objectId;
            _position = pickPosition;
        }
    }
}

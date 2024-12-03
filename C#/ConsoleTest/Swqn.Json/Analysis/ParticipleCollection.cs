using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Swqn.Json.Analysis
{
    internal class ParticipleCollection : ICollection<Participle>
    {
        public int Count => throw new NotImplementedException();

        bool ICollection<Participle>.IsReadOnly => throw new NotImplementedException();

        public void Add(Participle item)
        {
            throw new NotImplementedException();
        }

        void ICollection<Participle>.Clear()
        {
            throw new NotImplementedException();
        }

        bool ICollection<Participle>.Contains(Participle item)
        {
            throw new NotImplementedException();
        }

        void ICollection<Participle>.CopyTo(Participle[] array, int arrayIndex)
        {
            throw new NotImplementedException();
        }

        IEnumerator<Participle> IEnumerable<Participle>.GetEnumerator()
        {
            throw new NotImplementedException();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            throw new NotImplementedException();
        }

        bool ICollection<Participle>.Remove(Participle item)
        {
            throw new NotImplementedException();
        }
    }
}

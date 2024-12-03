using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Swqn.Json.Analysis
{
    internal class Tokenizer
    {
        public ParticipleCollection Parse(string jsonString) 
        {
            if (string.IsNullOrWhiteSpace(jsonString)) 
            {
                throw new ArgumentNullException(nameof(jsonString));
            }

            ParticipleCollection participles = new ParticipleCollection();
            int line = 1;
            int column = 1;

            for (int i = 0; i < jsonString.Length; i++)
            {
                if (char.IsWhiteSpace(jsonString[i]) == true)
                {
                    continue;
                }
                else if (jsonString[i] == '{')
                {
                    participles.Add(new Participle(ParticipleType.ObjectStart, jsonString[i].ToString(), line, column));
                }
                else if (jsonString[i] == '}')
                {
                    participles.Add(new Participle(ParticipleType.ObjectEnd, jsonString[i].ToString(), CharPosition.Empty));
                }
                else if (jsonString[i] == '[')
                {
                    participles.Add(new Participle(ParticipleType.ArrayStart, jsonString[i].ToString(), CharPosition.Empty));
                }
                else if (jsonString[i] == ']')
                {
                    participles.Add(new Participle(ParticipleType.ArrayEnd, jsonString[i].ToString(), CharPosition.Empty));
                }
                else if (jsonString[i] == ',')
                {
                    participles.Add(new Participle(ParticipleType.ElementSeparator, jsonString[i].ToString(), CharPosition.Empty));
                }
                else if (jsonString[i] == ':')
                {
                    participles.Add(new Participle(ParticipleType.KVSymbol, jsonString[i].ToString(), CharPosition.Empty));
                }
                else if (jsonString[i] == '"') 
                {
                    StringBuilder sb = new StringBuilder();
                    i++;
                    while (i < jsonString.Length)
                    {
                        
                    }
                }
            }

            return participles;
        }
    }
}

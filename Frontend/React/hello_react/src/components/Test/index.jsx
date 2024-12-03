import { useImmer } from 'use-immer';
import { useEffect,useState } from 'react';

function Test() {
  const [u, setU] = useState('gfdshfd')

  useEffect(() => {
    setU('gfdsgf')
  }, [u])

  return (
    <div className="Test">
      <header className="Test-header">
        <p>
          { 
            u
          }
        </p>
      </header>
    </div>
  );
}

export default Test;
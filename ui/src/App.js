import SockJS from 'sockjs-client';
import './App.css';
import CodeEditor from './components/CodeEditor';
import { useEffect, useState } from 'react';
import { over } from 'stompjs';

const socket = SockJS('http://localhost:8080/ws');
const stomClient = over(socket)

function App() {


  const [code,setCode] = useState('');
 


  useEffect(() => {
    stomClient.connect({},() => {
      stomClient.subscribe("/topic/codeUpdate",(message)=>{
        setCode(JSON.parse(message.body));
      })
    });
    return  () => {
      if (stomClient) {
        // stomClient.disconnect();
      }
    };
  },[])
  

  const handleCodeChange = (newCode) => {
      setCode(newCode);
      stomClient.send('/app/codeChange',{},JSON.stringify(newCode));
  }


  return (
    <div className="App">
      <header>Collabarative Code Editor</header>
      <CodeEditor value={code} onChange={handleCodeChange}/>
    </div>
  );
}

export default App;

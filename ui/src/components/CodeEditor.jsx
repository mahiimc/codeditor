import { Editor } from '@monaco-editor/react'
import axios from 'axios';
import React, { useRef, useState } from 'react'
import '../css/CodeEditor.css'

const CodeEditor = ({value,onChange}) => {

  const editorRef = useRef(null);
  const [code,setCode] = useState('');
  const [result,setResult] = useState('');
  

  const handleEditorDidMount = (editor,monaco) => {
      editorRef.current = editor;
  }
  const handleExecution = async () => {
    console.log(code);
    const response = await axios.post("http://localhost:8080/api/v1/code/",{
      code: code
    });
    setResult(response.data.result.output);
    console.log(response);
  }

  return (
    <div>
      <div className="main">
        <Editor 
          height="90vh"
          width='50vw'
          defaultLanguage='java'
          defaultValue='// Write your code here'
          value={value}
          onChange={(value,event) => { onChange(value); setCode(value)}}
          editorDidMount={handleEditorDidMount}
        
        />
      <div className='console'>
        <p> {result} </p>
      </div>
   </div>
    <input type='button' value='Execute' onClick={handleExecution}/>

   </div>

  )
}

export default CodeEditor
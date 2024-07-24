import { Editor } from '@monaco-editor/react'
import axios from 'axios';
import React, { useRef, useState } from 'react'

const CodeEditor = ({value,onChange}) => {

  const editorRef = useRef(null);
  const [code,setCode] = useState('');

  const handleEditorDidMount = (editor,monaco) => {
      editorRef.current = editor;
  }
  const handleExecution = async () => {
    console.log(code);
    const response = await axios.post("http://localhost:8080/api/v1/code/",{
      code: code
    });
    console.log(response);
  }

  return (
    <div>
   <Editor 
   
    height="90vh"
    defaultLanguage='java'
    defaultValue='\\ Write your code here'
    value={value}
    onChange={(value,event) => { onChange(value); setCode(value)}}
    editorDidMount={handleEditorDidMount}
  
   />
   <input type='button' value='Execute' onClick={handleExecution}/>
   </div>

  )
}

export default CodeEditor
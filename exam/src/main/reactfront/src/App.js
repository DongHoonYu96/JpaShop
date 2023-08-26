import './App.css';
import { useState } from "react";

const App = () => {
  const [text, setText] = useState("123");
  
  const [edit, setEdit] = useState(true);

  if (edit) {
    return(
      <div>
      <input type="text" value={text}
        //이벤트가 발생되면 (텍스트갱신) => 자동 랜더링됨
        //text는 event.target.value에 잇음
        onChange={(e) => {
          //console.log(e.target.value);
          setText(e.target.value);
      }}></input>
      <button onClick={()=>setEdit(false)}>수정</button>
        </div>
    )
  }
  else
  return (
    <div>{text}
      <button onClick={() => setEdit(true)}>수정</button>
    </div>
  )
}

export default App;

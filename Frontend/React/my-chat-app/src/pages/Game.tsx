import React, { useState, useEffect, useCallback, useRef } from 'react';

import { RoomCreate, CreateRoom, CreateConnection, StartConnection, CloseConnection } from '../utils/gameSignalR'



export default function Game() {
  const [color1, setColor1] = useState<string>()
  const [color2, setColor2] = useState<string>()
  const color1Ref = useRef<HTMLInputElement>(null)

  const RoomCreated = useCallback(function (roomName: string) {
    console.log(roomName)
  }, [])

  useEffect(() => {
    CreateConnection()
    StartConnection()
    RoomCreate.AddFunc(RoomCreated)
    console.log('开始发送消息')
    CreateRoom('王者荣耀')
    console.log('结束发送消息')
    return () => {
      RoomCreate.RemoveFunc(RoomCreated)
      CloseConnection()
    }
  }, [RoomCreated])

  const setColor = function() {
    const color1 = color1Ref.current?.value
    if (color1 === undefined || color1 === null) 
    {
      return
    }

    const r = parseInt(color1.slice(1, 3), 16);
    const g = parseInt(color1.slice(3, 5), 16);
    const b = parseInt(color1.slice(5, 7), 16);

    setColor1(color1)
    setColor2(`rgba(${r},${g},${b},0.1)`)

  }
   
  return (
    <div className="game">
      <div className="game-board">
        <button onClick={()=>{CreateRoom('wa')}}></button>
      </div>
      <div>
        <input type="color" ref={color1Ref} name="d" id="d" onChange={setColor}/>
      </div>
      <div style={{height: "100px", width: "100px", backgroundColor: color1}}></div>
      <div style={{height: "100px", width: "100px", backgroundColor: color2}}></div>
    </div>
  );
}

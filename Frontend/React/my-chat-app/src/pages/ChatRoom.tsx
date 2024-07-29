import React, { useState, useEffect, useRef } from 'react';
import * as signalR from '@microsoft/signalr';
import { MessageAndConsoleLogger, MessageLogger } from '../utils/logger';
import { Button, Input, InputRef, Space, Spin, message } from 'antd';

type Message = {
  user: string;
  message: string;
};

const ChatRoom = function () {
    const [messageApi, contextHolder] = message.useMessage();
    const [messages, setMessages] = useState<Message[]>([]);
    const [inputValue, setInputValue] = useState('');
    const [hubConnection, setHubConnection] = useState<signalR.HubConnection>();
    const [userName, setUserName] = useState<string>('');
    const userEl = useRef<InputRef>(null)
    const messageEl = useRef<InputRef>(null)

    const [messageAndConsoleLogger] = useState<MessageAndConsoleLogger>(new MessageAndConsoleLogger(signalR.LogLevel.Information));

    useEffect(() => {

      let connection: signalR.HubConnection;

      connection = new signalR.HubConnectionBuilder()
        .withUrl('http://danggui.xyz:5011/chatHub')
        // .withUrl('http://localhost:5011/gameHub')
        .withAutomaticReconnect()
        .configureLogging(messageAndConsoleLogger)
        .build();

      connection.start().then(() => {
        // messageAndConsoleLogger.log(signalR.LogLevel.Information, '连接到服务')

        connection.onclose(() => {
          // messageAndConsoleLogger.log(signalR.LogLevel.Information, '连接关闭中...');
        });
    
        connection.onreconnecting(() => {
          // messageAndConsoleLogger.log(signalR.LogLevel.Information, '重新连接中...');
        });

        connection.on('receiveMessage', (user: string, message: string) => {
          if(user !== userName) {
            setMessages((prevMessages) => [...prevMessages, { user, message }]);
          }
          // setMessages((prevMessages) => [...prevMessages, { user, message }]);
        });

        setHubConnection(connection);

        // messageAndConsoleLogger.log(signalR.LogLevel.Information, `连接完成, ${connection.connectionId}, ${connection.state}`)

      }).catch((error) => {
        // messageAndConsoleLogger.log(signalR.LogLevel.Error ,`连接失败, ${error}`)
      });

      return () => {
        let stopConnect = setInterval(()=>{
          if (connection && connection.state === signalR.HubConnectionState.Connected) {
            connection.stop().then(() => {
              // messageAndConsoleLogger.log(signalR.LogLevel.Information, `关闭服务, ${connection.connectionId}`);
            });
            clearInterval(stopConnect);
          }
        }, 100)

        // connection.stop().then(() => {
        //   messageAndConsoleLogger.log(signalR.LogLevel.Information, `关闭服务, ${connection.connectionId}`);
        // });
      };
    }, [userName, messageAndConsoleLogger]);
  
    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
      setInputValue(event.target.value);
    };
  
    const handleSendMessage = () => {
      if (userName.trim() === '') {
        //messageAndConsoleLogger.error('用户名不能为空')
        messageApi.warning('用户名不能为空', 1000)
        return;
      }
      
      if (inputValue.trim() !== '') {
        setMessages((prevMessages) => [
          ...prevMessages,
          { user: userName, message: inputValue },
        ]);
        setInputValue('');
        messageEl.current?.select();
  
        hubConnection?.invoke('sendMessage', userName, inputValue);
      }
    };

    const handleSetUserName = () => {
      if (userEl.current != null) 
      {
        if (userEl.current.input?.value != null && userEl.current.input.value !== undefined)
        {
          setUserName(userEl.current.input.value)
          messageEl.current?.select();
        }
      }
    }
  
    return (
      <div>
        <div>{contextHolder}</div>
        <Space size="middle">
          <Spin size="small" />
          <Spin />
          <Spin size="large" />
        </Space>
        <div>
          <Input ref={userEl} type="text" defaultValue={''}/>
          <Button type="primary" onClick={handleSetUserName}>SetName</Button>
        </div>
        <ul>
          {messages.map((message, index) => (
            <li key={index}>
              <strong>{message.user}: </strong>
              {message.message}
            </li>
          ))}
        </ul>
        <div>
          <Input ref={messageEl} type="text" value={inputValue} onChange={handleInputChange} />
          <Button type="primary" onClick={handleSendMessage}>Send</Button>
        </div>
      </div>
    );
};

export default ChatRoom;
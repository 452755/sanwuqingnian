//import React from 'react';
// import logo from './logo.svg';
import './App.css';
import ChatRoom from './pages/ChatRoom';
import Game from './pages/Game';
// import './utils/myMessageSignalR'

import { ArgumentNullError } from './components/recycler-view';

import { ConfigProvider, Form } from 'antd';
import FormItem from 'antd/es/form/FormItem';

function App() {

  // throw new ArgumentNullError("fdfddfg");
  // console.log(e)
  return (
    <ConfigProvider>
      <ChatRoom />
      {/* <Game /> */}
      <Form>
        <FormItem/>
      </Form>
    </ConfigProvider>
  );
}

export default App;

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useEffect, useState, useCallback} from 'react';
//import type {PropsWithChildren} from 'react';
import shotid from 'shortid';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  TextInput,
  Button,
  FlatList,
  Image,
  ImageSourcePropType,
} from 'react-native';

import {NavigationContainer} from '@react-navigation/native';

import * as signalR from '@microsoft/signalr';

import {
  /*Colors,*/
  /* DebugInstructions, */
  Header,
  /* ReloadInstructions, */
} from 'react-native/Libraries/NewAppScreen';

import {Colors} from './util/Colors';
import {Int32} from 'react-native/Libraries/Types/CodegenTypes';

type Message = {
  id: string;
  user: string;
  message: string;
};

let connection1: signalR.HubConnection;

type ImgListProp = {
  imgList?: ImageSourcePropType[];
  count?: Int32;
};

const ImgList = (props: ImgListProp) => {
  return (
    <View>
      {props.imgList?.map(img => {
        return <Image key={img.toLocaleString()} source={img} />;
      })}
    </View>
  );
};

const Item = (msg: Message) => {
  const [imgList, setImgList] = useState<ImageSourcePropType[]>([]);

  const AddImg = (img: ImageSourcePropType) => {
    setImgList([...imgList, img]);
  };

  useEffect(() => {
    if (imgList.length > 0) {
      return;
    }

    // AddImg(0);
  });

  return (
    <View style={styles.item}>
      <Text style={styles.title}>{msg.user + '----' + msg.message}</Text>
      <ImgList imgList={imgList} />
    </View>
  );
};

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode
      ? Colors.darker.toString()
      : Colors.pick.toString(),
  };

  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [hubConnection, setHubConnection] = useState<signalR.HubConnection>();

  const receiveMessage = useCallback((user: string, message: string) => {
    if (user !== 'Me') {
      setMessages(prevMessages => [
        ...prevMessages,
        {id: shotid.generate(), user, message},
      ]);
    }
  }, []);

  useEffect(() => {
    if (connection1) {
      return;
    }

    fetch('http://192.168.31.45:9993/TzxRestFulServer/app/login',).then().catch()

    const connection = new signalR.HubConnectionBuilder()
      .withUrl('http://danggui.xyz:5011/chatHub')
      .build();

    connection.start().then(() => {
      console.log('Connected to server');
    });

    connection.on('receiveMessage', receiveMessage);

    connection1 = connection;

    setHubConnection(connection);

    return () => {
      connection.stop();
    };
  }, [receiveMessage]);

  const handleSendMessage = () => {
    if (inputValue.trim() !== '') {
      setMessages(prevMessages => [
        ...prevMessages,
        {id: shotid.generate(), user: 'Me', message: inputValue},
      ]);
      setInputValue('');

      // Send message to server
      // The 'sendMessage' method will be handled by the server
      // and broadcasted to all connected clients
      hubConnection?.invoke('sendMessage', 'Me1', inputValue);
    }
  };

  return (
    <NavigationContainer>
      <SafeAreaView style={backgroundStyle}>
        <StatusBar
          barStyle={isDarkMode ? 'light-content' : 'dark-content'}
          backgroundColor={backgroundStyle.backgroundColor}
        />
        <Header />
        <View
          style={{
            backgroundColor: backgroundStyle.backgroundColor,
          }}>
          <FlatList
            data={messages}
            renderItem={item => (
              <Item
                message={item.item.message}
                id={item.item.id}
                user={item.item.user}
              />
            )}
            keyExtractor={item => item.id}
          />
        </View>
        <View
          style={{
            backgroundColor: isDarkMode
              ? Colors.black.toString()
              : Colors.white.toString(),
          }}>
          <Text>输入</Text>
          <TextInput
            style={styles.input}
            onChangeText={setInputValue}
            value={inputValue}
          />
          <Button
            onPress={handleSendMessage}
            title="发送"
            color="#841584"
            accessibilityLabel="Learn more about this purple button"
          />
        </View>
      </SafeAreaView>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
  item: {
    backgroundColor: '#f9c2ff',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
  title: {
    fontSize: 32,
  },
});

export default App;

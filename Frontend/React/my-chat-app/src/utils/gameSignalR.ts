import * as signalR from '@microsoft/signalr'
import { MessageAndConsoleLogger } from './logger'

class UpdateFn {
  private _funcs: Function[]

  constructor() {
    this._funcs = []
  }

  invoke(params: any) {
    console.log(this._funcs)
    if (this._funcs !== void 0 && this._funcs !== null && this._funcs.length > 0){
      this._funcs.forEach((_func) => {
        // 这里需要处理 this 指向
        _func(params)
      })
    }
  }

  public AddFunc(func: Function) {
    this._funcs.push(func)
  }

  public RemoveFunc(func: Function) {
    const index = this._funcs.findIndex((_func) => _func === func)
    this._funcs.splice(index, 1)
  }
}

export const RoomCreate: UpdateFn = new UpdateFn()

let connection: signalR.HubConnection
const messageAndConsoleLogger: MessageAndConsoleLogger = new MessageAndConsoleLogger(signalR.LogLevel.Information)

export function CreateConnection() {
  messageAndConsoleLogger.info('创建连接')
  if (connection !== void 0 && connection !== null) {
    CloseConnection()
  }
  connection = new signalR.HubConnectionBuilder()
    // .withUrl('http://danggui.xyz:5011/gameHub')
    .withUrl('http://localhost:5011/gameHub')
    .withAutomaticReconnect()
    .configureLogging(messageAndConsoleLogger)
    .build();
}

export function StartConnection() {
  messageAndConsoleLogger.info('开启连接')
  if (connection !== void 0 && connection !== null) {
    connection.start().then(() => {
      connection.onclose(() => {
        
      });
      
      connection.onreconnecting(() => {
        
      });
      
      connection.on('opponentMoves', (i: number) => {
        
      })
      
      connection.on('NewRoomCreated', (roomName: string) => {
        RoomCreate.invoke(roomName);
      })
    }).catch((error) => {
          
    });    
  }
}

export function CloseConnection() {
  messageAndConsoleLogger.info('关闭连接')
  if (connection !== void 0 && connection !== null) {
    switch (connection.state) {
      case signalR.HubConnectionState.Connected:
        connection.stop()
        break;
      case signalR.HubConnectionState.Disconnected:
      case signalR.HubConnectionState.Disconnecting:
        break;
      case signalR.HubConnectionState.Connecting:
      case signalR.HubConnectionState.Reconnecting:
        const handle = setTimeout(() => {
          CloseConnection()
        }, 10)
        break;
    }
  }
}

export function CreateRoom(roomName: string) {
  console.log('创建房间')
  if (connection !== void 0 && connection !== null && connection.state === signalR.HubConnectionState.Connected) {
    connection.send('CreateGameRoom', roomName)
  }
}
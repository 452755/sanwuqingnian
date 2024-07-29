import * as signalR from '@microsoft/signalr'
import { MessageAndConsoleLogger } from './logger'

console.log('gfdagh')

const logger: MessageAndConsoleLogger = new MessageAndConsoleLogger(signalR.LogLevel.Information);

let connection: signalR.HubConnection = new signalR.HubConnectionBuilder()
  .configureLogging(logger)
  .withUrl('http://localhost:5011/myMessageHub')
  .withAutomaticReconnect()
  .build()

connection.start().then(() => {
  connection.on('SendMyMessage', (name, message) => {
    logger.info(`${name} --- ${message}`)
  })
  connection.send('SendMessage', '王者', 'gfsdfghfdsh')
})
.catch((err) => {
  logger.error(err.toString())
})

export default connection
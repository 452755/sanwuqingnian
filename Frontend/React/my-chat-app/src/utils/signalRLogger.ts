import { ILogger, LogLevel as signalRLogLevel } from "@microsoft/signalr";
import { ConsoleLogger } from "@microsoft/signalr/dist/esm/Utils";

import {message as Message} from 'antd'

interface ILoggerEx {
    error(message: any): void,
    warn(message: any): void,
    info(message: any): void,
};

// export enum LogLevel = signalRLogLevel;

// export class LoggerBuilder {
//   bulider(): ILogger {
//     return new ConsoleLogger(LogLevel.Information);
//   }
// }
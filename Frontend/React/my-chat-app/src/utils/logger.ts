import { ILogger, LogLevel } from "@microsoft/signalr";
import { ConsoleLogger } from "@microsoft/signalr/dist/esm/Utils";

import {message as Message} from 'antd'

interface ILoggerEx {
    error(message: any): void,
    warn(message: any): void,
    info(message: any): void,
};

export class MessageLogger implements ILogger, ILoggerEx {
    private readonly _minLevel: LogLevel;

    constructor(minimumLogLevel: LogLevel) {
        this._minLevel = minimumLogLevel;
    }

    error(message: any): void {
        this.log(LogLevel.Error, message)
    }
    warn(message: any): void {
        this.log(LogLevel.Warning, message)
    }
    info(message: any): void {
        this.log(LogLevel.Information, message)
    }

    public log(logLevel: LogLevel, message: string): void {
        if (logLevel >= this._minLevel) {
            const msg = `[${new Date().toISOString()}] ${LogLevel[logLevel]}: ${message}`;
            
            switch (logLevel) {
                case LogLevel.Critical:
                case LogLevel.Error:
                    //Message.error(msg)
                    break;
                case LogLevel.Warning:
                    //Message.warning(msg);
                    break;
                case LogLevel.Information:
                    //Message.info(msg);
                    break;
                default:
                    // console.debug only goes to attached debuggers in Node, so we use console.log for Trace and Debug
                    //Message.info(msg);
                    break;
            } 
        }
    }
}

export class MessageAndConsoleLogger extends ConsoleLogger implements ILoggerEx {
    private readonly minLevel: LogLevel;

    constructor(minimumLogLevel: LogLevel = LogLevel.Information) {
        super(minimumLogLevel)
        this.minLevel = minimumLogLevel;
        this.out = console;
    }

    error(message: any): void {
        this.log(LogLevel.Error, message)
    }
    warn(message: any): void {
        this.log(LogLevel.Warning, message)
    }
    info(message: any): void {
        this.log(LogLevel.Information, message)
    }

    log(logLevel: LogLevel, message: string): void {
        super.log(logLevel, message)

        if (logLevel >= this.minLevel) {
            const msg = `[${new Date().toISOString()}] ${LogLevel[logLevel]}: ${message}`;

            switch (logLevel) {
                case LogLevel.Critical:
                case LogLevel.Error:
                    Message.error(msg, 10)
                    break;
                case LogLevel.Warning:
                    Message.warning(msg, 10);
                    break;
                case LogLevel.Information:
                    Message.info(msg, 10);
                    break;
                default:
                    // console.debug only goes to attached debuggers in Node, so we use console.log for Trace and Debug
                    Message.info(msg, 10);
                    break;
            } 
        }
    }
}
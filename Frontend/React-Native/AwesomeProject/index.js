/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';

let str = AppRegistry.registerComponent(appName, () => App, true);

console.log('wang' + str);

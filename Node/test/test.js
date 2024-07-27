const publicIp = require('public-ip'); // 获取外网ip
//import publicIp from 'public-ip';
publicIp.v4().then(ip => console.log(ip)); // 调用模块提供的api即可获取
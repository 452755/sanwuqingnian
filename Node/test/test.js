const publicIp = require('public-ip'); // 获取外网ip
//import publicIp from 'public-ip';
publicIp.v4().then(ip => console.log(ip)); // 调用模块提供的api即可获取

const CryptoJS = require('crypto-js')

var string = '06121904108'

const Base64 = require('./base64')

//md5加密
let md5_password = CryptoJS.MD5(string).toString();

console.log(md5_password) //b068931cc450442b63f5b3d276ea4297

//SHA1
let sHA1 = CryptoJS.SHA1(string).toString()

console.log(sHA1)

//AES加密 第一个参数为需要加密的内容，第二个参数为秘钥 
let mys = CryptoJS.AES.encrypt('my message', 'secret key 123').toString()
console.log(mys)   //U2FsdGVkX1+m1zTtrXxMvwh0qPUGOyRn+wO5w+0fz2Q=

let d_mys = CryptoJS.AES.decrypt('U2FsdGVkX1+m1zTtrXxMvwh0qPUGOyRn+wO5w+0fz2Q=', 'secret key 123').toString(CryptoJS.enc.Utf8)
console.log(d_mys)   //my message

var b = new Base64();
var str = b.encode('admin:admin');
alert('base64 encode:' + str); //解密
str = b.decode(str);
alert('base64 decode:' + str);
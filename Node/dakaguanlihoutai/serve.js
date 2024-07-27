// src/server.js
const express = require('express');
const { resolve } = require('path');
const { promisify } = require('util');
const fs = require('fs');
const { log } = require('console');
const cors=require('cors');
const { json } = require('express');
const { lookup } = require('dns');
const util = require('util')
// const http = require('http')
const httprequest = require('request')
const rp  = require('request-promise');
var bodyParser = require('body-parser');
const sqlite3 = require('sqlite3');

var db = null;

const server = express();
const port = parseInt(process.env.PORT || '80');
const publicDir = resolve('public');

const filepath = `${publicDir}/json/userinfo.json`

server.use(cors())
server.use(bodyParser.json());
server.use(bodyParser.urlencoded({extended: false}));

//设置跨域访问
server.all('/api/*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
    res.header("X-Powered-By",' 3.2.1')
    res.header("Content-Type", "application/json;text/css;charset=utf-8");
    next();
});

server.get('/api/getuser', function (request, response) {
    fs.readFile(filepath, function(err,data){
        if(err){
            log("err:"+err)
            response.end("err:"+err)
        }
        else{
            log(data.toString()+"\n\n")
            response.header('content-type','text/plain; charset=UTF-8')
            response.end(data.toString())
        }
    })
})

server.get('/api/delete', function(request,response){
    try {
        log(request.query.id)
        var userinfo=JSON.parse(fs.readFileSync(filepath,'utf8'));
        var dakalist = userinfo.dakalist.filter(value=>value.id!==request.query.id)
        userinfo.dakalist=dakalist
        log(userinfo.dakalist)
        log(dakalist)
        fs.writeFileSync(filepath,JSON.stringify(userinfo))
        response.end('删除成功')
    } catch (error) {
        log(error)
        response.end('删除失败'+error)   
    }
})

server.post('/api/modify',function(request,response){
    try {
        log(request.body)
        let body = request.body
        var userinfo=JSON.parse(fs.readFileSync(filepath,'utf8'));
        userinfo.dakalist.forEach(value => {
            if(value.id===body.id){
                log(value)
                value.name=body.name
                value.password=body.password
                value.address=body.address
                value.school=body.school
                value.schoolid=body.schoolid
            }
        });
        log(userinfo.dakalist.filter(v=>v.id===body.id)[0])
        fs.writeFileSync(filepath,JSON.stringify(userinfo))
        response.end('修改成功')
    } catch (error) {
        log(error)
        response.end('修改失败'+error)   
    }
})

server.post('/api/add',function(request,response){
    try {
        log(request.body)
        var userinfo=JSON.parse(fs.readFileSync(filepath,'utf8'));
        userinfo.dakalist.push(request.body)
        log(userinfo.dakalist.filter(v=>v.id===request.body.id)[0])
        fs.writeFileSync(filepath,JSON.stringify(userinfo))
        response.end('添加成功')
    } catch (error) {
        log(error)
        response.end('添加失败'+error)   
    }
})

server.get('/api/daka', function(request,response){
    try{
        let userinfo=JSON.parse(fs.readFileSync(filepath,'utf8'))
        let dakalist= userinfo.dakalist
        let result=''
        if(request.query.id){
            let dakaitem = dakalist.filter(v=>v.id === request.query.id)[0]
            daka(dakaitem, (dakaresult) => {
                result = dakaresult
                console.log(result)
                response.end(result)
            })
        }
        else{
            for(let i =0; i < dakalist.length;i++){
                let dakaitem = dakalist[i]
                daka(dakaitem, (dakaresult) => {
                    result += dakaresult +'\n'                      
                })
            }
            let timer = setTimeout(()=>{
                console.log(result)
                response.end(result)
            },dakalist.length*500)
        }
    }
    catch(error){
        log(error)
        response.end(error.message)
    }
})

function daka(dakaitem,callback){
    let result=''
    try{
        let url = 'https://jiankang.suoeryun.com/api/userManager/passageway/pclogin'
        let headers = {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json;charset=UTF-8',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.81'
        }
        let data = {
            f_number: dakaitem['name'],
            f_password: dakaitem['password'],
            f_schoolid: dakaitem['schoolid'],
        }
        httprequest({
            url: url,
            method: "POST",
            headers: headers,
            body: JSON.stringify(data)
        }, function(error, response, body) {
            if (!error && response.statusCode == 200) {
                let loginresponsedata = JSON.parse(body)
                url = 'https://jiankang.suoeryun.com/api/outbreakRegistered/createOutbreakRegistered'
                headers = {
                    'Accept': 'application/json, text/plain, */*',
                    'Content-Type': 'application/json;charset=UTF-8',
                    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36(KHTML,likeGecko)Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.81',
                    'Authorization': loginresponsedata.data.token
                }
                data = {
                    f_currentdetailsaddress: dakaitem.address,
                    f_currentlocation: "61",
                    f_daily_temperature: "36.5",
                    f_is_chest_tightness: 0,
                    f_is_confirm_contact: 0,
                    f_is_confirm_contact_address: "",
                    f_is_confirm_contact_time: "",
                    f_is_confirmed: 0,
                    f_is_cough: 0,
                    f_is_fever: 0,
                    f_is_high_risk_address: "",
                    f_is_high_risk_come: 0,
                    f_is_high_risk_time: "",
                    f_is_nausea_emesis: 0,
                    f_is_quarantine_home: 0,
                    f_is_quarantine_medicine: 0,
                    f_is_rhinitis: 0,
                    f_is_suspected: 0,
                    f_is_suspected_contact: 0,
                    f_is_suspected_contact_time: "",
                    f_see_doctor_explain: "",
                    f_symptom_explain: "",
                }
                httprequest({
                    url: url,
                    method: "POST",
                    headers: headers,
                    body: JSON.stringify(data)
                },function(error,response,body){
                    let dakaresponsedata=JSON.parse(body)
                    if(!error && response.statusCode == 200){
                        if(dakaresponsedata.message === ''){
                            result = dakaitem.id+' 打卡成功'
                            callback(result)
                        }
                        else{
                            result = dakaitem.id+' 已打卡，'+dakaresponsedata.message
                            callback(result)
                        }
                    }
                    else{
                        result = dakaitem.id+' 打卡失败'
                        callback(result)
                    }
                })
            }
            else{
                result = dakaitem.id+'登录失败'
                callback(result)
            }
        }); 
    }
    catch(err){
        console.log(dakaitem.id+'打卡失败：'+err)
        result = dakaitem.id+'打卡失败：'+err.message
        callback(result)
    }
}

function connectdb(){
    if(fs.existsSync(`${publicDir}/db/dakadb.db`)){

    }
}

async function bootstrap() {
  server.use(express.static(publicDir));
  await promisify(server.listen.bind(server, port))();
  log(`>  - Local:   http://localhost:${port}/ `);
}

bootstrap();

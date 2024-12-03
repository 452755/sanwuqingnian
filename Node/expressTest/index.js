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

const server = express();
const port = parseInt(process.env.PORT || '9000');
const publicDir = resolve('public');

const filepath = `${publicDir}/json/userinfo.json`

server.use(cors())
server.use(bodyParser.json());
server.use(bodyParser.urlencoded({extended: false}));


function daka(options,callback){
    let result=''
    try{
        let ss = options.ss;
        let url = '';
        let headers = {
            'Accept': 'application/json, text/javascript, */*; q=0.01',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
            'Content-Type': 'application/json; charset=UTF-8',
            'Host': `${options.api}.tzxpos.com`,
            'Origin': `https://${options.api}.tzxpos.com`,
            'Pragma': 'no-cache',
            'Referer': `https://${options.api}.tzxpos.com/index.html`,
            'sec-ch-ua': '"Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104"',
            'sec-ch-ua-mobile': '?0',
            'sec-ch-ua-platform': '"Windows"',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36',
            'X-Requested-With': 'XMLHttpRequest',
        }
        let data = {};
        url = `https://${options.api}.tzxpos.com/rcsystem/web/shop/guqing/delete`
        data = {
            wid: '',
            ss: ss,
            scode: options.scode
        }
        httprequest({
            url: url,
            method: "POST",
            headers: headers,
            body: JSON.stringify(data)
        }, function(error, response, body) {
            if (!error && response.statusCode == 200){
                
            }
            else{
                result = '失败' + response + error
                callback(result)
                return;
            }
        });
        url = `https://${options.api}.tzxpos.com/rcsystem/web/shop/goods/list`
        // headers = {
        //     'Accept': 'application/json, text/plain, */*',
        //     'Content-Type': 'application/json;charset=UTF-8',
        //     'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.81'
        // }
        //{"page":1,"limit":20,"ss":"676cdb66dd714a0cb95afe9eb0c7a962","scode":20000549}
        data = {
            page: 1,
            limit: 1000,
            ss: ss,
            scode: options.scode
        }
        httprequest({
            url: url,
            method: "POST",
            headers: headers,
            body: JSON.stringify(data)
        }, function(error, response, body) {
            //console.log(body)
            //console.log(response)
            if (!error && response.statusCode == 200) {
                let loginresponsedata = JSON.parse(body)
                console.log(loginresponsedata)
                    loginresponsedata.data.forEach(element => {
                        url = `https://${options.api}.tzxpos.com/rcsystem/web/shop/guqing/add`
                        // headers = {
                        //     'Accept': 'application/json, text/plain, */*',
                        //     'Content-Type': 'application/json;charset=UTF-8',
                        //     'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36(KHTML,likeGecko)Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.81',
                        // }
                        //{"ss":"676cdb66dd714a0cb95afe9eb0c7a962","scode":20000549,"webNo":"1626893196890873","gqAmount":"1","operatorName":"管理员","gqTime":1668572086,"jsTime":1668614399,"mark":"","specs":""}
                        data = {
                            ss: ss,
                            scode: options.scode,
                            webNo: element.vWebNo,
                            gqAmount: 1,
                            operatorName: '管理员',
                            gqTime: 1668572086,
                            jsTime: 1668614399,
                            mark: "",
                            specs: element.vSpec
                        }
                        console.log(data)
                        httprequest({
                            url: url,
                            method: "POST",
                            headers: headers,
                            body: JSON.stringify(data)
                        },function(error,response,body){
                            let dakaresponsedata=JSON.parse(body)
                            if(!error && response.statusCode == 200){
                                if(dakaresponsedata.msg === ''){
                                    result = '成功'
                                    callback(result)
                                }
                                else{
                                    result = dakaresponsedata.msg
                                    callback(result)
                                }
                            }
                            else{
                                result = '失败'
                                callback(result)
                            }
                        })
                    });
            }
            else{
                result = '失败'
                callback(result)
            }
        }); 
    }
    catch(err){
        console.log('打卡失败：'+err)
        result = '打卡失败：'+err.message
        callback(result)
    }
}

function guqing(){
    var Connection = require('tedious').Connection;  
    var config = {  
        server: '192.168.31.45,59157',  //update me
        authentication: {
            type: 'default',
            options: {
                userName: 'sa', //update me
                password: '123456'  //update me
            }
        },
        options: {
            // If you are on Microsoft Azure, you need encryption:
            encrypt: false,
            database: 'tzx_zf_dd_008'  //update me
        }
    }; 
    var connection = new Connection(config);  
    connection.on('connect', function(err) {  
        // If no error, then good to proceed.  
        console.log("Connected");  
        executeStatement();  
    });  
    
    connection.connect();
  
    var Request = require('tedious').Request;  
    var TYPES = require('tedious').TYPES;  
  
    function executeStatement() {  
        request = new Request("select * from cy_jczl_cpzl_lb", function(err) {  
        if (err) {  
            console.log(err);}  
        });  
        var result = "";  
        request.on('row', function(columns) {  
            columns.forEach(function(column) {  
              if (column.value === null) {  
                console.log('NULL');  
              } else {  
                result+= column.value + " ";  
              }  
            });  
            console.log(result);  
            result ="";  
        });  
  
        request.on('done', function(rowCount, more) {  
            console.log(rowCount + ' rows returned');  
        });  
        
        // Close the connection after the final event emitted by the request, after the callback passes
        request.on("requestCompleted", function (rowCount, more) {
            connection.close();
        });
        connection.execSql(request);  
    }  
}

var arguments = process.argv;
let argOptions = {
    api: 'dev',
    user: '',
    pwd: '',
    scode: 0,
    ss: '',
};
// 可以使用循环迭代所有的命令行参数（包括node路径和文件路径）
arguments.forEach((val, index) => {
    console.log(`${index}: ${val}`);
    if(val.startsWith("api=")){
        argOptions.api = val.split("=")[1];
    }
    if(val.startsWith("user=")){
        argOptions.user = val.split("=")[1];
    }
    if(val.startsWith("pwd=")){
        argOptions.pwd = val.split("=")[1];
    }
    if(val.startsWith("scode=")){
        argOptions.scode = val.split("=")[1];
    }
    if(val.startsWith("ss=")){
        argOptions.ss = val.split("=")[1];
    }
});

daka(argOptions, (result)=>{console.log(result)})
//guqing();



            List<cy_spgq> spgqModelList = GoodsGQManager.Instance.getAllSpgqList();
            cy_spgq spgqModel = spgqModelList.FirstOrDefault(spgq =>
                spgq.i_goods_pk == this.m_currentSelectedCpzlModel.pk
                && spgq.v_specs == this.m_currentSelectedCpzlModel.v_specs
                && spgq.i_status == 0
                && (spgq.t_gq_time < DateTime.Parse(this.dateTimePickerGuQingEnd.Text) || spgq.t_js_time < DateTime.Parse(this.dateTimePickerGuQingStart.Text)));

            if (spgqModel != null) 
            {
                MessageBoxFunction.showWarningMessageBox("此菜品的估清时间到结束时间之内，已有有效估清，不能重复添加！请修改估清时间，或者结束时间");
                this.dateTimePickerGuQingStart.Select();
                return;
            }
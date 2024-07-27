//file:test.js
var sqlite3 = require('sqlite3');
var db = new sqlite3.Database('./tmp/1.db');

db.exec('create table test1(name varchar(15))', function (err, res) {
    console.log(err);
    console.log(res);
});
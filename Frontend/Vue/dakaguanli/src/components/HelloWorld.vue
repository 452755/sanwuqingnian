<template>
  <div>
    <div>
      <div id="addormodify" v-show="addormodifyshow">
        <div class="form-group text-center"><h4><strong>{{ addormodify?"添加":"修改" }}</strong></h4></div>
        <div class="form-group"><input class="form-control" v-model="daka.id" type="hidden"/></div>
        <div class="form-group"><input class="form-control" v-model="daka.name" placeholder="学号" type="txt"/></div>  
        <div class="form-group"><input class="form-control" v-model="daka.password" placeholder="密码" type="txt"/></div>
        <div class="form-group">
          <select class="form-control" v-model="daka.schoolid">
            <option value="0">---请选择---</option>  
            <option v-for="item in querySchool" :key="item.f_id" v-bind:value="item.f_id">
              {{ item.f_schoolname }}
            </option>
          </select>
        </div>
        <div class="form-group"><input class="form-control" v-model="daka.address" placeholder="地址" type="txt"/></div>
        <div class="form-group text-right">
          <input @click="queding" type="button" class="btn btn-primary" value="确定"/>
          <input @click="quxiao" type="button" class="btn btn-defuat" value="取消"/>
        </div>
      </div>    
      <div class="text-right">
        <input @click="ondaka('')" type="button" class="btn btn-success" value="全部打卡"/>
        <input @click="tianjia" type="button" class="btn btn-primary" value="添加"/>
      </div>
      <div>
        <table class="table">
          <thead>
            <tr>
              <td>编号</td>
              <td>学号</td>
              <td>密码</td>
              <td>学校</td>
              <td>地址</td>
              <td>操作</td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in dakalist" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.password }}</td>
              <td>{{ item.school }}</td>
              <td>{{ item.address }}</td>
              <td>
                <button class="btn btn-info" @click="xiugai(item.id)">修改</button>
                <button class="btn btn-danger" @click="shanchu(item.id)">删除</button>
                <button class="btn btn-success" @click="ondaka(item.id)">打卡</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>

import axios from 'axios'
import shortId from 'shortid'
import swal from 'sweetalert'
let base64 = require('js-base64').Base64

export default {
  name: 'HelloWorld',
  data(){
    return{
      addormodifyshow:false,
      addormodify:true,
      dakalist:[],
      daka:{
        id:"",
        name:"",
        password:"",
        address:"",
        school:"",
        schoolid:0
      },
      querySchool:[],
      houtaipath:'http://182.92.207.85:80/api',
      jiamidaka:{
        id:"",
        name:"",
        password:"",
        address:"",
        school:"",
        schoolid:0
      }
    }
  },
  methods:{
    randomString(e) {    
      e = e || 32;
      var t = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      a = t.length,
      n = "";
      for (let i = 0; i < e; i++) n += t.charAt(Math.floor(Math.random() * a));
      return n
    },
    jiami(str){
      let jiamistr=base64.encode(str)
      let s1 = jiamistr.substring(0,2)
      let s2 = jiamistr.substring(2)
      jiamistr=s1+this.randomString(5)+s2+this.randomString(5)
      return jiamistr
    },
    jiemi(str){
      let s1 = str.substring(0,2)
      let s2 = str.substring(7,str.length-5)
      let jiemistr=base64.decode(s1+s2)
      return jiemistr
    },
    quxiao(){
      this.daka={
        id:"",
        name:"",
        password:"",
        address:"",
        school:"",
        schoolid:"0"
      }
      this.addormodifyshow=false
    },
    tianjia(){
      this.addormodify=true
      this.addormodifyshow = true
    },
    queding(){
      this.daka.school=this.querySchool.filter(value=>value.f_id===this.daka.schoolid)[0].f_schoolname
      if(this.addormodify){
        this.daka.id=shortId.generate()
        this.daka.name=this.jiami(this.daka.name)
        this.daka.password=this.jiami(this.daka.password)
        this.add()
      }
      else{
        this.daka.name=this.jiami(this.daka.name)
        this.daka.password=this.jiami(this.daka.password)
        this.modify()
      }
      this.daka={
        id:"",
        name:"",
        password:"",
        address:"",
        school:"",
        schoolid:"0"
      }
      this.addormodifyshow=false
    },
    ondaka(id){
      if(id!==''){
        this.clock(id)
      }
      else{
        this.dakalist.forEach(item => {
          this.clock(item.id)
        })
      }  
    },
    shanchu(id){
      this.delete(id)
    },
    xiugai(id){
      this.addormodify=false
      let daka=JSON.parse(JSON.stringify(this.dakalist.filter(value => value.id === id)[0]))
      this.daka=daka
      this.addormodifyshow=true
    },
    fetch(){
      axios.get(`${this.houtaipath}/getuser`)
      .then(res => {
        let dakalist=res.data.dakalist
        dakalist.forEach(value => {
          value.name=this.jiemi(value.name)
          value.password=this.jiemi(value.password)
        });
        this.dakalist=dakalist
        this.querySchool=res.data.querySchool
      })
      .catch(err => {
        console.error(err); 
      })
    },
    add(){
      axios.post(`${this.houtaipath}/add`,this.daka )
      .then(res => {
        alert(res.data)
        this.fetch()
      })
      .catch(err => {
        console.error(err); 
      })
    },
    modify(){
      axios.post(`${this.houtaipath}/modify`,this.daka )
      .then(res => {
        alert(res.data)
        this.fetch()
      })
      .catch(err => {
        console.error(err); 
      })
    },
    clock(id){
      axios.get(`${this.houtaipath}/daka`,{params: { id }})
      .then(res => {
        swal({
          title: "Good job!",
          text: res.data,
          icon: "success",
          button: "Aww yiss!",
        });
      })
      .catch(err => {
        console.error(err)
      })
    },
    delete(id){
      axios.get(`${this.houtaipath}/delete`,{ params: { id }})
      .then(res => {
        alert(res.data)
        this.fetch()
      })
      .catch(err => {
        console.error(err); 
      })
    }
  },
  created(){
    this.fetch()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  #addormodify{
    width: 300px;
    height: 280px;
    background: #e79c9c;
    position: absolute; 
    left: 50%; 
    top: 50%;
    margin-top: -120px;    /* 高度的一半 */
    margin-left: -150px;    /* 宽度的一半 */
  }
</style>

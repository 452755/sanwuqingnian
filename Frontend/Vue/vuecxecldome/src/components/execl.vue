<template>
  <div class="hello" v-loading="loading"
    element-loading-text="">
    <h1>{{ msg }}</h1>
    <!-- 导入Excel -->
    <el-upload
       action="/上传文件的接口"
       :on-change="onChange"
       :auto-upload="false"
       :show-file-list="true"
       accept=".xls, .xlsx" >
       <el-button type="warning" icon="el-icon-folder-add">批量导入</el-button>
    </el-upload>

    <el-button type="warning" icon="el-icon-download" @click="exportToExcel">批量导出</el-button>
    <el-progress type="dashboard" :percentage="50">
      <template #default="{ percentage }">
        <span class="percentage-value">{{ percentage }}%</span>
        <span class="percentage-label">当前进度</span>
      </template>
    </el-progress>
     <el-progress :percentage="50">
  <el-button type="text">自定义内容</el-button>
</el-progress>
<el-progress :text-inside="true" :stroke-width="20" :percentage="50" status="exception">
  <span>自定义内容</span>
</el-progress>
<el-progress type="circle" :percentage="100" status="success">
  <el-button type="success" icon="el-icon-check" circle></el-button>
</el-progress>
<el-progress type="dashboard" :percentage="80">
  <template #default="{ percentage }">
    <span class="percentage-value">{{ percentage }}%</span>
    <span class="percentage-label">当前进度</span>
  </template>
</el-progress>
  </div>
</template>
 
<script>
  export default {
    data() {
      return {
        msg:"导出导入数据",
        fileData: null,
        studentlist:[
          {
            id: 1,
            stuNumber: "wangzhe",
            stuName: "wangzhe",
            sex: "男",
            tel: "17351717939",
            xueyuan: "机电工程学院",
            banji: "计算机高职1904班",
            lyNumber: "4",
            sushe: "209"
          },{
            id: 1,
            stuNumber: "wangzhe",
            stuName: "wangzhe",
            sex: "男",
            tel: "17351717939",
            xueyuan: "机电工程学院",
            banji: "计算机高职1904班",
            lyNumber: "4",
            sushe: "209"
          },{
            id: 1,
            stuNumber: "wangzhe",
            stuName: "wangzhe",
            sex: "男",
            tel: "17351717939",
            xueyuan: "机电工程学院",
            banji: "计算机高职1904班",
            lyNumber: "4",
            sushe: "209"
          },{
            id: 1,
            stuNumber: "wangzhe",
            stuName: "wangzhe",
            sex: "男",
            tel: "17351717939",
            xueyuan: "机电工程学院",
            banji: "计算机高职1904班",
            lyNumber: "4",
            sushe: "209"
          }
        ],
        outputs:[],
        loading:false,
        loadingtext:"",
      }
    },
    methods: {
      // ----------以下为导入Excel数据功能--------------
      // 文件选择回调
      onChange(file, fileList){
         console.log(fileList)
         this.fileData = file // 保存当前选择文件
         this.readExcel(); // 调用读取数据的方法
      },
      // 读取数据
      readExcel() {
       let that = this;
       const files = that.fileData;
       if (!files) {
         //如果没有文件
         return false;
       } else if (!/\.(xls|xlsx)$/.test(files.name.toLowerCase())) {
         this.$message.error("上传格式不正确，请上传xls或者xlsx格式");
         return false;
       }
       const fileReader = new FileReader();
       fileReader.onload = ev => {
         try {
           const data = ev.target.result;
           const workbook = this.XLSX.read(data, {
             type: "binary"
           });
           if (workbook.SheetNames.length >= 1) {
             this.$message({
               message: "导入数据表格成功",
               showClose: true,
               type: "success"
             });
           }
           const wsname = workbook.SheetNames[0]; //取第一张表
           const ws = this.XLSX.utils.sheet_to_json(workbook.Sheets[wsname]); //生成json表格内容
           //console.log(ws);
           that.outputs = []; //清空接收数据
           for (var i = 0; i < ws.length; i++) {
             var sheetData = {
               // 键名为绑定 el 表格的关键字，值则是 ws[i][对应表头名]
               id: ws[i]["id"],
               stuNumber: ws[i]["学号"],
               stuName: ws[i]["姓名"],
               sex: ws[i]["性别"],
               tel: ws[i]["电话"],
               xueyuan: ws[i]["学院"],
               banji: ws[i]["班级"],
               lyNumber: ws[i]["楼宇号"],
               sushe: ws[i]["宿舍号"]
             };
             that.studentlist.push(sheetData);
           }
           this.$refs.upload.value = "";
         } catch (e) {
           return false;
         }
       };
       // 如果为原生 input 则应是 files[0]
       fileReader.readAsBinaryString(files.raw);
       console.log(that.studentlist);
     },
     // ----------以下为导出Excel数据功能--------------
      exportToExcel() {
      //excel数据导出
        require.ensure([], () => {
          const { export_json_to_excel } = require("../excel/Export2Excel");
          const tHeader = [
            "id",
            "学号",
            "姓名",
            "性别",
            "联系电话",
            "宿舍号",
            "学院",
            "班级",
            "楼宇号"
          ];
          const filterVal = [
            "id",
            "stuNumber",
            "stuName",
            "sex",
            "tel",
            "sushe",
            "xueyuan",
            "banji",
            "lyNumber"
          ];
          const list = this.studentlist;
          const data = this.formatJson(filterVal, list);
          export_json_to_excel(tHeader, data, "学生列表excel");
        });
      },
      formatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => v[j]));
      }
    }
  }
</script>
<style scoped>
 
</style>
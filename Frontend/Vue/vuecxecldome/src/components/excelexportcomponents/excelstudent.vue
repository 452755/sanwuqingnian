<template>
  <el-button type="primary" icon="el-icon-download" @click="exportToExcel"
    >批量导出</el-button
  >
</template>

<script>

export default ({
    data(){
        return {
            studentExeclComponent: {
                tHeader: [
                    "id",
                    "学号",
                    "姓名",
                    "性别",
                    "联系电话",
                    "宿舍号",
                    "学院",
                    "班级",
                    "楼宇号"
                ],
                filterVal: [
                    "id",
                    "stuNumber",
                    "stuName",
                    "sex",
                    "tel",
                    "sushe",
                    "xueyuan",
                    "banji",
                    "lyNumber"
                ],
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
                ]
            }
        }
    },

    methods:{
        // ----------以下为导出Excel数据功能--------------
      exportToExcel() {
        const studentExeclComponent = this.studentExeclComponent;
        //excel数据导出
        require.ensure([], () => {
          const { export_json_to_excel } = require("../../excel/Export2Excel");
          const data = this.formatJson(studentExeclComponent.filterVal, studentExeclComponent.list);
          export_json_to_excel(studentExeclComponent.tHeader, data, "学生列表excel");
        });
      },
      formatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => v[j]));
      }
    }
})
</script>


<style scoped>
</style>
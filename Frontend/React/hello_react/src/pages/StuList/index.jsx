import React, { Component } from 'react'
import { nanoid } from 'nanoid'
import PubSub from 'pubsub-js'

import Item from '../../components/Item/index'
import Footer from '../../components/Footer'

export default class StuList extends Component {

    state={
        stuList:[
            {
                id:nanoid(),
                name:'张三',
                age:18,
                sex:'男',
                isGraduation:false
            },{
                id:nanoid(),
                name:'李四',
                age:19,
                sex:'男',
                isGraduation:true
            },{
                id:nanoid(),
                name:'王五',
                age:17,
                sex:'男',
                isGraduation:false
            },{
                id:nanoid(),
                name:'舞幽幽',
                age:19,
                sex:'女',
                isGraduation:false
            }
        ]
    }

    componentDidMount(){
        //订阅删除单个学生的消息
        this.tokenDeleteStuNews = PubSub.subscribe('Delete_Stu',(_,subObj)=>{
			let {stuList} = this.state
            let newStuList = stuList.filter((stuObj)=>{
                return stuObj.id !== subObj.id
            })
            this.setState({stuList:newStuList})
		})
        //订阅删除所有已毕业的学生的消息
        this.tokenDeleteGraduation = PubSub.subscribe('Delete_Graduation',(_,subObj)=>{
            let {stuList} = this.state
            let newStuList = stuList.filter((stuObj)=>{
                return stuObj.isGraduation === false
            })
            this.setState({stuList:newStuList})
        })
        this.tokenAddStu=PubSub.subscribe('Add_Stu',(_,subObj)=>{
            let {stuList} = this.state
            let newStuList=[subObj,...stuList]
            this.setState({stuList:newStuList})
        })
    }

    componentWillUnmount(){
        PubSub.unsubscribe(this.tokenDeleteStuNews)
        PubSub.unsubscribe(this.tokenDeleteGraduation)
        PubSub.unsubscribe(this.tokenAddStu)
    }

    render() {
        let {stuList} = this.state
        return (
            <div>
                <table className="table table-hover table-bordered">
                    <thead>
                        <tr>
                            <th className="text-center">学生编号</th>
                            <th className="text-center">姓名</th>
                            <th className="text-center">年龄</th>
                            <th className="text-center">性别</th>
                            <th className="text-center">是否毕业</th>
                            <th className="text-center">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        {stuList.map((stuObj)=>{
                            return <Item key={stuObj.id} {...stuObj}/>
                        })}
                    </tbody>
                </table>
                <Footer {...this.state}/>
            </div>
        )
    }
}

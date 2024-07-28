import React, { Component } from 'react'
import Pubsub from 'pubsub-js'
import { Link } from 'react-router-dom'

export default class Item extends Component {

    //删除学生信息方法
    DeleteStu=(id,name)=>{
        return ()=>{
            if(!window.confirm(`是否要删除${name}学生的信息`)) return
            Pubsub.publish('Delete_Stu',{id:id})
        }
    }

    render() {
        let {id,name,age,sex,isGraduation} = this.props
        return (
            <tr>
                <td>{id}</td>
                <td>{name}</td>
                <td>{age}</td>
                <td>{sex}</td>
                <td>{isGraduation?'已毕业':'未毕业'}</td>
                <td>
                    <Link className="btn btn-warning" to={`/updateStu/${id}`}>更新</Link>
                    <span>&nbsp;</span>
                    <button className="btn btn-danger" onClick={this.DeleteStu(id,name)}>删除</button>
                </td>
            </tr>
        )
    }
}


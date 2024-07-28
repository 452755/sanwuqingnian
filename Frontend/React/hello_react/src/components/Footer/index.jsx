import React, { Component } from 'react'
import PubSub from 'pubsub-js'

export default class Footer extends Component {

    state={mouse:false}

    DeleteDeleteGraduation=()=>{
        PubSub.publish('Delete_Graduation',{})
    }

    HandleMouse=(flag)=>{
        return ()=>{
            this.setState({mouse:flag})
        }
    }

    render() {
        let {stuList}= this.props
        const isGraduation = stuList.reduce((pre,stu)=> pre + (stu.isGraduation ? 1 : 0),0)
        const unGraduation = stuList.reduce((pre,stu)=> pre + (stu.isGraduation ? 0 : 1),0)
        let stuCount=stuList.length
        return (
            <div style={{height: '40px',lineHeight: '40px', backgroundColor:this.state.mouse?'#15151515':'white'}} 
                className="container footer text-right" onMouseEnter={this.HandleMouse(true)} 
                onMouseLeave={this.HandleMouse(false)}>
                未毕业{unGraduation}人/已毕业{isGraduation}人/共{stuCount}人
                <button onClick={this.DeleteDeleteGraduation} className="btn btn-danger">清除所有已毕业学生</button>
            </div>            
        )
    }
}

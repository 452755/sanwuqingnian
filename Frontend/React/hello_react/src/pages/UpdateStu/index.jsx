import React, { Component } from 'react'

export default class index extends Component {
    upStuObj=
        {
            id:'',
            name:'',
            age:0,
            sex:'男',
            isGraduation:false
        }

    render() {
        this.upStuObj.id = this.props.match.params.id

        return (
            <div>
               
            </div>
        )
    }
}

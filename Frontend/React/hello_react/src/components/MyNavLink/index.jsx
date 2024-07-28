import React, { Component } from 'react'
import {NavLink} from 'react-router-dom'

export default class index extends Component {
    render() {
        let {to} = this.props
        return (
            <NavLink className="btn btn-primary"  to={to}>添加学生信息</NavLink>
        )
    }
}

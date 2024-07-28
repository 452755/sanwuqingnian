import React, { Component } from 'react'
import {Route,NavLink, Switch,Redirect} from 'react-router-dom'

import StuList from './pages/StuList'
import AddStu from './pages/AddStu'
import UpdateStu from './pages/UpdateStu'

import './app.css'

export default class App extends Component {
  render() {
    return (
      <div className="container">
        <h1 className="text-center">学生信息管理</h1>
        <ul className="nav nav-pills">
          <NavLink activeClassName="navActive" className="btn btn-primary"  to="/addStu">添加学生信息</NavLink>
          <NavLink activeClassName="navActive" className="btn btn-primary" to="/stuList">查看所有学生信息</NavLink>
        </ul>
        <div>
          <Switch>
            <Route path="/stuList" component={StuList}/>
            <Route path="/addStu" component={AddStu} />
            <Route path="/updateStu/:id" component={UpdateStu} />
            <Redirect to="/stuList"/>
          </Switch>
        </div>
      </div>
    )
  }
}


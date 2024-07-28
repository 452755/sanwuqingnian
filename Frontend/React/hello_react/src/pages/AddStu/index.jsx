import React, { Component } from 'react'
import PubSub from 'pubsub-js'
import { nanoid } from 'nanoid'

export default class AddStu extends Component {

    stuObj={
        id:nanoid(),
        name:'',
        age:0,
        sex:'男',
        isGraduation:false
    }

    AddStu=()=>{
        PubSub.publish('Add_Stu',this.stuObj)
    }

    render() {
        return (
            <div>
                <div className="form-group">
                  <label>Email address</label>
                  <input type="email" className="form-control" id="exampleInputEmail1" placeholder="Email"/>
                </div>
                <div className="form-group">
                  <label>Password</label>
                  <input type="password" className="form-control" id="exampleInputPassword1" placeholder="Password"/>
                </div>
                <div className="form-group">
                  <label>File input</label>
                  <input type="file" id="exampleInputFile"/>
                  <p className="help-block">Example block-level help text here.</p>
                </div>
                <div className="checkbox">
                  <label>
                    <input type="checkbox"/> Check me out
                  </label>
                </div>
            </div>
        )
    }
}

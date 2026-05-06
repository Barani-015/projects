import React, { useState } from 'react'
import axios from 'axios'
import {Link} from 'react-router-dom'

function login() {

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();

    const update=()=>{
        axios.post('http://127.0.0.1:6006/login',{username,password})
        .then(result=> console.log(result))
        .catch(err => console.log(err))
    }

  return (
      <div className='farmerlogin-con'>
            <form onSubmit={update}>
            <div className='details'>
                <input type='text' placeholder='Enter UserName' onChange={(e)=>setUsername(e.target.value)} />
                <input type='password' placeholder='Enter Password' onChange={(e)=>setPassword(e.target.value)}/>
            </div>
            <div className='button'>
                <Link to='/loginfarmer' >Log In</Link>
                <Link to='/createfarmer' >New Register</Link>
            </div>
            </form>
        </div>
    
  )
}

export default login
import React from 'react'
import {Link} from 'react-router-dom'
import {useState} from 'react'
import axios from 'axios'


function consumerlogin() {

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();

    const check =(e)=>{
        e.preventDefault();
        axios.post('http://localhost:6006/conlogin',{username,password})
        .then(result=>console.log(result))
        .catch(err => console.log(err))
    }

  return (
    
        <div className='farmerlogin-con'>
            <div className='details'>
                <form onSubmit={check}>
                    <input type='text' placeholder='Enter UserName' onChange={(e)=>setUsername(e.target.value)} />
                    <input type='password' placeholder='Enter Password' onChange={(e)=>setPassword(e.target.value)}/>
                </form>
            </div>
            <div className='button'>
                <Link to='/consumerpage'>Login</Link>
                <Link to='/createconsumer'>Register</Link>
            </div>
        </div>
    
  )
}

export default consumerlogin
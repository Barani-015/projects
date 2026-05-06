import React from 'react'
import {useState} from 'react'
import axios from 'axios'
import {useNavigate} from 'react-router-dom'

function createconsumer() {

    const [name, setName] = useState();
    const [email,setEmail] = useState();
    const [phno, setPhno] = useState();
    const [password, setPassword] = useState();
    const navigate = useNavigate();

    const create =(e)=>{
        e.preventDefault();
        axios.post('http://127.0.0.1:6006/create-con',{name,email,phno,password})
        .then(result=> {
            console.log(result)
            navigate('/consumerpage')
        })
        .catch(err => console.log(err))
    }

  return (
    <div className='createcon-form'>
        <form onSubmit={create}>
            <input type='text' placeholder='Enter Name' onChange={(e)=>setName(e.target.value)} />
            <input type='email' placeholder='Enter Email' onChange={(e)=>setEmail(e.target.value)} />
            <input type='number' placeholder='Enter Phno' onChange={(e)=>setPhno(e.target.value)} />
            <input type='password' placeholder='Enter Password' onChange={(e)=>setPassword(e.target.value)}/>
            <button>Create</button>
        </form>
    </div>
  )
}

export default createconsumer
import React, { useState } from 'react'
import {Link} from 'react-router-dom'
import axios from 'axios'

function createFarmer() {

    const [name, setName] = useState();
    const [userName, setUserName] = useState();
    const [adhar, setAdhar] = useState();
    const [patta, setPatta] = useState();
    const [location, setLocation] = useState();
    const [password, setPassword] = useState();

    const create = (e) =>{
        e.preventDefault();
        axios.post("http://127.0.0.1:6006/create",{name,userName,adhar,patta,location,password})
        .then(result => console.log(result))
        .catch(err => console.log(err))
    }

  return (
    <div className='createFarmer'>
        <form onSubmit={create}>
          <h2>Add New Customer</h2>
          <div className='mb-2'>
            <label htmlFor=''>Name</label>
            <input type='text' placeholder='Enter Name' className='form-control' onChange={(e)=>setName(e.target.value)} />
          </div>
          <div className='mb-2'>
            <label htmlFor=''>Enter Adhar</label>
            <input type='number' placeholder='Enter Adhar' className='form-control' onChange={(e)=>setAdhar(e.target.value)} />
          </div>
          <div className='mb-2'>
            <label htmlFor=''>Enter UserName</label>
            <input type='text' placeholder='Enter UserName' className='form-control' onChange={(e)=>setUserName(e.target.value)} />
          </div>
          <div className='mb-2'>
            <label htmlFor=''>Enter Location</label>
            <input type='text' placeholder='Enter Location' className='form-control' onChange={(e)=>setLocation(e.target.value)} />
          </div>
          <div className='mb-2'>
            <label htmlFor=''>Enter Patta Number</label>
            <input type='number' placeholder='Enter Patta' className='form-control' onChange={(e)=>setPatta(e.target.value)} />
          </div>
          <div className='mb-2'>
            <label htmlFor=''>Enter Password</label>
            <input type='password' placeholder='Enter Password' className='form-control' onChange={(e)=>setPassword(e.target.value)} />
          </div>
          <button>Create</button>
        </form>
    </div>
  )
}

export default createFarmer
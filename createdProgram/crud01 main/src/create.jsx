import React, { useState } from 'react'
import axios from 'axios'

function create() {

    const [name, setName] = useState();
    const [age, setAge] = useState();
    const [email, setEmail] = useState();
    const [phno, setPhno] = useState();

    const submit =(e)=>{
        e.preventDefault();
        axios.post('http://localhost:2006/create',{name, age, email, phno})
        .then(result => console.log(result))
        .catch(err => console.log(err))
    }
  return (
    <div className='d-flex justify-content-center align-items-center vh-100 bg-secondary'>
        <div className='w-50 rounded-3 p-3 bg-white'>
            <h3>Add User</h3>
            <form onSubmit={submit}>
                <div className='mb-2'>
                    <label htmlFor=''>Name</label>
                    <input type='text' placeholder='Enter Name' className='form-control' onChange={(e)=>setName(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Age</label>
                    <input type='number' placeholder='Enter Age' className='form-control' onChange={(e)=>setAge(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Email</label>
                    <input type='email' placeholder='Enter Email' className='form-control' onChange={(e)=>setEmail(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Phno</label>
                    <input type='number' placeholder='Enter Phno' className='form-control' onChange={(e)=>setPhno(e.target.value)} />
                </div>
                <button className='btn btn-success'>Create</button>
            </form>
        </div>
    </div>
  )
}

export default create
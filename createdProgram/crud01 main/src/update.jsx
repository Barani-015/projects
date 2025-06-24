import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom';

function update() {

  const {id} = useParams();
  const [name, setName] = useState();
  const [age, setAge] = useState();
  const [email, setEmail] = useState();
  const [phno, setPhno] = useState();

  useEffect(()=>{
      axios.get('http://localhost:2006/getUser/'+id)
      .then(result =>{
        console.log(result)
        setName(result.data.name)
        setAge(result.data.age)
        setEmail(result.data.email)
        setPhno(result.data.phno)
      })
      .catch(err => console.log(err))
  },[])

  return (
    <div className='d-flex justify-content-center align-items-center vh-100 bg-primary'>
      <div className='w-50 rounded-3 p-3 bg-white'>
        <form onSubmit={update}>
        <div className='mb-2'>
                    <label htmlFor=''>Name</label>
                    <input type='text' placeholder='Enter Name' className='form-control' value={name} onChange={(e)=>setName(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Age</label>
                    <input type='number' placeholder='Enter Age' className='form-control' value={age} onChange={(e)=>setAge(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Email</label>
                    <input type='email' placeholder='Enter Email' className='form-control' value={email} onChange={(e)=>setEmail(e.target.value)} />
                </div>
                <div className='mb-2'>
                    <label htmlFor=''>Phno</label>
                    <input type='number' placeholder='Enter Phno' className='form-control' value={phno} onChange={(e)=>setPhno(e.target.value)} />
                </div>
        </form>
      </div>
    </div>
  )
}

export default update
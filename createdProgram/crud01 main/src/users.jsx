import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'

function users() {

    const [users , setUsers] = useState([]);

    useEffect(()=>{
        axios.get('http://localhost:2006')
        .then(result=> setUsers(result.data))
        .catch(err => console.log(err))
    },[])

    const handleDelete=(id)=>{
        axios.delete('http://localhost:2006/delete/'+id)
        .then(result=>{
            console.log(result)
            window.location.reload()
        })
        .catch(err => console.log(err))
    }

  return (
    <div className='d-flex justify-content-center align-items-center bg-primary vh-100'>
        <div className='w-90 rounded-3 p-3 bg-white'>
            <Link to ='/create' className='btn btn-success'>Add +</Link>
            <table className='table'>
                <thead>
                    <td>Name</td>
                    <td>Age</td>
                    <td>Email</td>
                    <td>Phno</td>
                    <td>Actions</td>
                </thead>
                <tbody>
                    {
                        users.map((user)=>{
                                return <tr>
                                    <td>{user.name}</td>
                                    <td>{user.age}</td>
                                    <td>{user.email}</td>
                                    <td>{user.phno}</td>
                                    <td>
                                        <Link to={`/update/${user._id}`} className='btn btn-success'>Update</Link>
                                        <button className='btn btn-danger' onClick={()=>handleDelete(user._id)}>Delete</button>
                                    </td>
                                </tr>
                        })
                    }
                </tbody>
            </table>
        </div>
    </div>
  )
}

export default users
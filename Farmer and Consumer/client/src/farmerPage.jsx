import React, { useState, useEffect } from 'react'
import axios from 'axios'
import {Link} from 'react-router-dom'

function farmerPage() {

  const [details, setDetails] = useState([]);
  const [product, setProduct] = useState();
  const [quantity, setQuantity] = useState();

  useEffect(()=>{
    axios.get('http://localhost:6006/getData')
    .then(result=>{
      setDetails(result.data)
    })
    .catch(err=> console.log(err))
  },[])

  const submit=()=>{
    axios.post('http://127.0.0.1:6006/products/',{product,quantity})
    .then(result => {
      console.log(result)
      window.location.reload();
    })
      .catch(err=> console.log(err))
  }

  const handleDelete=(id)=>{
    axios.delete('http://localhost:6006/delete/'+id)
    .then(result => {
      console.log(result);
      window.location.reload();
    })
    .catch(err => console.log(err))
  }

  return (
    <>
    <Link to='/farmerHome'>Home</Link>
    <div className='setValues'>
    <form action={submit}>
    <input type='text' placeholder='Enter Product' onChange={(e)=>setProduct(e.target.value)} />
    <input type='number' placeholder='Enter quantity in Kilograms' onChange={(e)=>setQuantity(e.target.value)} />
    {/* <input type='submit' value={'submit'}></input> */}
    <button>Submit</button>
    </form>
    </div>
    <table className='table'>
      <thead>
        <tr>
          <td>Products</td>
          <td>Quantity</td>
          <td>Actions</td>
        </tr>
      </thead>
      <tbody>
        {
          details.map((detail)=>{
            return <tr>
              <td>{detail.product}</td>
              <td>{detail.quantity}</td>
              <td>
                <Link to={`/update/${detail._id}`}><button>Update</button></Link>
                <button onClick={(e)=>handleDelete(detail._id)}>Delete</button>
              </td>
            </tr>
          })
        }
      </tbody>
    </table>
    </>
  )
}

export default farmerPage
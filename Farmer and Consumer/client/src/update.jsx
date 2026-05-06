import React from 'react'
import {useParams} from 'react-router-dom'
import {useState} from 'react'
import { useNavigate } from 'react-router-dom';
import axios from 'axios' 


function update() {
    const {id} = useParams();

    const [product, setProduct] = useState();
    const [quantity, setQuantity] = useState();
    const navigate = useNavigate();

    const update=(e)=>{
        e.preventDefault();
        axios.put('http://127.0.0.1:6006/update/'+id,{product,quantity})
        .then(result => {
            console.log(result)
            navigate('/loginfarmer')
        })
        .catch(err => console.log(err))
    }

  return (
    <div className='update-datas'>
        <form onSubmit={update}>
        <input type='text' placeholder='Enter Product' onChange={(e)=>setProduct(e.target.value)} />
        <input type='number' placeholder='Enter Quantity' onChange={(e)=>setQuantity(e.target.value)} />
        <button>Update</button>
        </form>
    </div>
  )
}

export default update
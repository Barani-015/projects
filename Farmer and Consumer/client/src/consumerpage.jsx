import React from 'react'
import { useState,useEffect } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'

function consumerpage() {

  const [prods, setProds] = useState([]);

  useEffect(()=>{
    axios.get('http://localhost:6006/getProd')
    .then(result=> setProds(result.data))
    .catch(err => console.log(err))
  },[])

  const handletext=(id)=>{
      let text = document.getElementById('value')
      text.innerText = "Done"

      let opm = document.getElementById("output-msg")
      opm.innerText="You purchasing the product of tomato"
      }

  return (
    <div>
      <h2>Product Details</h2>
      <Link to='/farmerHome'>Home</Link>
      <div className='prod-con'>
        <table className='table'>
          <thead>
            <tr>
              <td>Products</td>
              <td>Quantity</td>
              <td>Req Quanity</td>
              <td>Action</td>
            </tr>
          </thead>
          <tbody>
            {
              prods.map((prod,index)=>{
                return <tr>
                  <td>{prod.product}</td>
                  <td>{prod.quantity+"  Kg"}</td>
                  <td>{<input type='number' />}</td>
                  <td><button onClick={(e)=>handletext(prod._id)}><p id='value'>Buy</p></button></td>
                </tr>
              })
            }
          </tbody>
        </table>
        <p id='output-msg'></p>
      </div>
    </div>
  )
}

export default consumerpage
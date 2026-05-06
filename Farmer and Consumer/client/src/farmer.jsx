import React from 'react'
import {Link } from 'react-router-dom'
import Fac from './assets/farmerconsumer.jpg';

function farmer() {
  return (
    
    <div className='btn'>
        {/* <img src={Fac} className='background-img' /> */}
        <div className='links'>
        <Link to='/farmerlogin' className='farmer'>Farmer</Link>
        <Link to='/consumerlogin' className='consumer'>Consumer</Link>
        </div>
        </div>
    
    
  )
}

export default farmer
import React from 'react'
import kelo from './assets/Untitled.png';
import {Link} from 'react-router-dom'


function index() {
  return (
    <div className='main-app'>
    <img className='farmer-img' src={kelo} alt='no define' />
    <h1>Welcome</h1>
    <div className='linkHome'>
    <Link to ='/farmerHome'>Open</Link>
    </div>
  </div>
  )
}

export default index
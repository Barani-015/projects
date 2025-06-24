import React from 'react'
import 'bootstrap/dist/css/bootstrap.min.css'
import {BrowserRouter, Routes, Route } from 'react-router-dom'
import Users from './users'
import Create from './create'
import Update from './update'


function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route path='/' element={<Users />} />
            <Route path='/create' element={<Create />}/>
            <Route path='/update' element={<Update />}/>
        </Routes>
    </BrowserRouter>
  )
}

export default App
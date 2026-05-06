import React from 'react';
import './app.css';
import 'bootstrap/dist/css/bootstrap.min.css'
import {BrowserRouter , Routes , Route} from 'react-router-dom';
import Index from './index'
import Farmer from './farmer'
import Farmerlogin from './farmerlogin'
import Consumerlogin from './consumerlogin';
import CreateFarmer from './createFarmer';
import FarmerPage from './farmerPage';
import Update from './update'
import Createconsumer from './createconsumer'
import Consumerpage from './consumerpage';

function App() {
  return (
   <>
   <BrowserRouter>
    <Routes>
      <Route path='/' element={<Index />} />
      <Route path='/farmerHome' element={<Farmer />} />
      <Route path='/farmerlogin' element={<Farmerlogin />} />
      <Route path='/consumerlogin' element={<Consumerlogin />}/>
      <Route path='/createfarmer' element={<CreateFarmer />} />
      <Route path='/loginfarmer' element={<FarmerPage />} />
      <Route path='/update/:id' element={<Update />}/>
      <Route path='/createconsumer' element={<Createconsumer />} />
      <Route path='/consumerpage' element={<Consumerpage />} />
    </Routes>
   </BrowserRouter>
   </>
  )
}

export default App
const express = require('express')
const mongoose = require('mongoose')
const cors = require('cors')
const UserModel = require('./model/userModel');


const app= express();
app.use(cors());
app.use(express.json())

mongoose.connect('mongodb://localhost:27017/storage')

app.post('/create',(req,res)=>{
    UserModel.create(req.body)
    .then(collections => res.json(collections))
    .catch(err => res.json(err))
})

app.get('/',(req,res)=>{
    UserModel.find({})
    .then(collections => res.json(collections))
    .catch(err => res.json(err))
})

app.delete('/delete/:id',(req,res)=>{
    const id = req.params.id
    UserModel.findByIdAndDelete({_id:id})
    .then(collections => res.json(collections))
    .catch(err => res.json(err))
})

app.get('/getUser/:id',(req,res)=>{
    const id = req.params.id;
    UserModel.findById({_id:id})
    .then(collections => res.json(collections))
    .catch(err => res.json(err))
})
app.listen(2006,()=>{
    console.log("Server is Running...")
})
const express = require('express')
const mongoose = require('mongoose')
const cors = require('cors')
const userModel = require('./farmuserModel/farmUserModel');
const userPD = require('./product/productUM');
const userCM = require('./con-from/userCM')

mongoose.connect('mongodb://127.0.0.1:27017/fac')

const app = express();
app.use(cors())
app.use(express.json())

app.post('/create',(req,res)=>{
    userModel.create(req.body)
    .then(farmers => res.json(farmers))
    .catch(err => res.json(err))
})

app.post('/login',(req,res)=>{
    const {username, password} = req.body;
    userModel.findOne({username: name})
    .then(username =>{
        if(username){
            if(username.password === password){
                res.json("success")
            }
            else{
                res.json("failed")
            }
        }else{
            res.json("no record exists")
        }
    })
})

app.post('/products',(req,res)=>{
        userPD.create(req.body)
        .then(productlists => res.json(productlists))
        .catch(err => res.json(err))
})
app.get('/getData',(req,res)=>{
    userPD.find({})
    .then(productlists => res.json(productlists))
    .catch(err => res.json(productlists))
})

app.put('/update/:id',(req,res)=>{
    const id = req.params.id
    userPD.findByIdAndUpdate({_id:id},{
        product:req.body.product,
        quantity:req.body.quantity
})
    .then(productlists => res.json(productlists))
    .catch(err => res.json(err))
})

app.delete('/delete/:id',(req,res)=>{
    const id = req.params.id
    userPD.findByIdAndDelete({_id:id})
    .then(productlists => res.json(productlists))
    .catch(err => res.json(err))
})

app.post('/create-con',(req,res)=>{
    userCM.create(req.body)
    .then(conusers => res.json(conusers))
    .catch(err => res.json(err))
})

app.post('/conlogin',(req,res)=>{
    const {username, password} = req.body;
    userPD.findOne({username: name})
    .then(username =>{
        if(username){
            if(username.password === password){
                res.json("success")
            }
            else{
                res.json("failed")
            }
        }else{
            res.json("no record exists")
        }
    })
})

app.get('/getProd',(req,res)=>{
    userPD.find({})
    .then(productlists=> res.json(productlists))
    .catch(err => res.josn(err))
})

app.listen(6006,()=>{
    console.log("Server is Running at port 6006")
})
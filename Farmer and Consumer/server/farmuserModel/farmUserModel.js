const mongoose = require('mongoose')

const userShcema = new mongoose.Schema({
    name:String,
    userName:String,
    adhar:Number,
    patta:Number,
    location:String,
    password:String
})

const userModel = new mongoose.model("farmers",userShcema)

module.exports = userModel
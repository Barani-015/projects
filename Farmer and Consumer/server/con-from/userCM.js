const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
    name:String,
    email:String,
    phno:Number,
    password:String
})

const userModel = new mongoose.model("conusers",userSchema)

module.exports = userModel
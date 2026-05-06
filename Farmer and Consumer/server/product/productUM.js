const mongoose = require('mongoose')

const userShema = new mongoose.Schema({
    product:String,
    quantity:Number
})

const userModel = new mongoose.model("productLists",userShema)

module.exports = userModel
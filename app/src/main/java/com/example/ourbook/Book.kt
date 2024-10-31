package com.example.ourbook

data class Book(val id:Int, val name:String, val surename:String, val email:String,
                val address:String, val date:String, val hp:String, val image: ByteArray?=null)

package com.mazzoca.imgconv

import scala.collection.mutable.HashMap 

class ConvertOption {

  var formatName: String = null 

  var copyright: Boolean = false

  var device: Device = null  

  var displayWidth: Int = 240  
  var displayHeight: Int = 320 

  var params = new HashMap[String, String]
}

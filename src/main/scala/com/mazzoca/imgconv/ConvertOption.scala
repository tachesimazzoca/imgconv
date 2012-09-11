package com.mazzoca.imgconv

import scala.collection.mutable.HashMap 

class ConvertOption {

  var device: Option[Device] = None

  var formatName: Option[String] = None

  var copyright: Boolean = false

  var params = new HashMap[String, String]
}

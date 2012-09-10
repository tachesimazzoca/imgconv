package com.mazzoca.imgconv.plugins

import java.io.{InputStream, OutputStream}

trait Plugin {

  def execute(input: InputStream, output: OutputStream)
}

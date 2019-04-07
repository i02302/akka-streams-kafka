package com.i02302.config

import com.typesafe.config.ConfigFactory

trait ApplicationConfig {

  lazy val AppConf = ConfigFactory.load

}

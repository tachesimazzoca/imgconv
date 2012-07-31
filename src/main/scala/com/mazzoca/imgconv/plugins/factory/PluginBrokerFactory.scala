package com.mazzoca.imgconv.plugins.factory

import com.mazzoca.imgconv.ConvertOption
import com.mazzoca.imgconv.plugins.PluginBroker

trait PluginBrokerFactory {

    def create(option:ConvertOption): PluginBroker
}

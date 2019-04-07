package com.i02302.actor.worker

import com.i02302.actor.Responder
import com.i02302.protocol.LauncherRequest

trait Worker[O <: Any] extends Responder[LauncherRequest, O]

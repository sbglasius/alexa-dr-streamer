package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class LaunchRequestHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, LaunchRequest)
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug("Launch intent")
        return say(input, "Welcome to the unofficial Denmark's Radio skill. Which station do you want to listen to?")
    }
}

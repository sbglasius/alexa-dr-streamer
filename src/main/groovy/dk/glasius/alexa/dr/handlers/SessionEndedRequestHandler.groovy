package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import com.amazon.ask.model.SessionEndedRequest
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SessionEndedRequestHandler implements RadioRequestHandler {

    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, SessionEndedRequest)
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug("Session ended")
        return input.getResponseBuilder().build()
    }
}


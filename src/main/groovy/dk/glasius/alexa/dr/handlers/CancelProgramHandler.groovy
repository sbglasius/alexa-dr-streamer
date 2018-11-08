package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class CancelProgramHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, 'AMAZON.CancelIntent')
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug("Cancel intent")
        say input, "Stopping Denmark's Radio. GoodBye."
    }
}

package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class PauseOrStopProgramHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, "AMAZON.PauseIntent", "AMAZON.StopIntent")
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        cancelPlayer input, "Pausing Denmark's Radio. Say 'resume' to continue.", false
    }
}

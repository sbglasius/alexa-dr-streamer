package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class HelpIntentHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, 'AMAZON.HelpIntent', 'HelpIntent')
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug("Help intent")
        return say(input, "To select a program say 'P' followed by the a number from 1 through 8, for example you could say 'P 1'. To listen to P 4 regional stations say 'play region', followed by the region name. To listen to the hourly news loop say 'play news'")
    }
}

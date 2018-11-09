package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.Slot
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SelectProgramHandler implements RequestHandler, RadioRequestHandler {

    Properties stationList

    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, "SelectProgramIntent")
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Slot> slots = getSlots(input)

        Integer program = slots.program.value?.toInteger()
        log.debug("Got slot 'program' with value ${program}")

        if (program < 0 || program > 8) {
            def response = "Could not find the right program. Programs are a number between 1 and 8. For a P 4 regional station, say 'region', followed by the region name"
            def reprompt = "Say a number between 1 and 8. For a P 4 regional station, say 'region' followed by the region name"
            return ask(input, response, reprompt)
        }

        String propertyName = "p$program"
        if (propertyName == 'p4') {
            def p4response = "To listen to a P 4 regional station, say 'region', followed by the region name"
            return say(input, p4response)
        }

        String programName = stationList.getProperty("${propertyName}.name")
        String streamUrl = stationList.getProperty("${propertyName}.url")
        String streamToken = getRequestId(input) + streamUrl

        setSessionAttribute(input, PROGRAM_NAME, programName)
        setSessionAttribute(input, STREAM_URL, streamUrl)
        setSessionAttribute(input, STREAM_TOKEN, streamToken)

        startPlayer(input, "Starting $programName", streamUrl, streamToken)
    }


}

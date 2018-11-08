package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class ResumeProgramHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, "AMAZON.ResumeIntent")
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        String programName = getSessionAttribute(input, PROGRAM_NAME)
        if (!programName) {
            return say(input, "Could not continue playback")
        }
        log.debug("Resuming playback of $programName")
        String streamUrl = getSessionAttribute(input, STREAM_URL)
        String streamToken = getSessionAttribute(input, STREAM_TOKEN)
        String speechText = "Resuming Denmark's Radio '${programName}'"
        startPlayer(input, speechText, streamUrl, streamToken)
    }
}

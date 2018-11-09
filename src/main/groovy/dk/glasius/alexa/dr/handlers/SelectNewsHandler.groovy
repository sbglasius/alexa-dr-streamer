package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SelectNewsHandler implements RadioRequestHandler {

    Properties stationList

    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, "SelectNewsIntent")
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        log.debug("Playing news and sports loop")

        String programName = stationList.getProperty("news.name")
        String streamUrl = stationList.getProperty("news.url")
        String streamToken = getRequestId(input) + streamUrl

        setSessionAttribute(input, PROGRAM_NAME, programName)
        setSessionAttribute(input, STREAM_URL, streamUrl)
        setSessionAttribute(input, STREAM_TOKEN, streamToken)

        startPlayer(input, "Now starting '${programName}'", streamUrl, streamToken)
    }


}

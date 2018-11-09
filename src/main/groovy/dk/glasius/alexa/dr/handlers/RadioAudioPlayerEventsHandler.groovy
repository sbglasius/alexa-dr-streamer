package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class RadioAudioPlayerEventsHandler implements RadioRequestHandler {
    @Override
    boolean canHandle(HandlerInput handlerInput) {
        return matches(handlerInput, 'AudioPlayer.PlaybackStarted', 'AudioPlayer.PlaybackNearlyFinished', 'AudioPlayer.PlaybackFinished', 'AudioPlayer.PlaybackStopped', 'AudioPlayer.PlaybackFailed')
    }

    @Override
    Optional<Response> handle(HandlerInput handlerInput) {
        log.debug("Ignoring $handlerInput.request.type")
        return handlerInput.getResponseBuilder().build()
    }
}

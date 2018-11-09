package dk.glasius.alexa.dr.handlers

import com.amazon.ask.attributes.AttributesManager
import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Intent
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Request
import com.amazon.ask.model.Response
import com.amazon.ask.model.Slot
import com.amazon.ask.model.interfaces.audioplayer.ClearBehavior
import com.amazon.ask.model.interfaces.audioplayer.PlayBehavior
import com.amazon.ask.request.Predicates
import com.amazon.ask.response.ResponseBuilder
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
trait RadioRequestHandler implements RequestHandler {

    static final String PROGRAM_NAME = 'programName'
    static final String STREAM_URL = 'streamUrl'
    static final String STREAM_TOKEN = 'streamToken'

    private static Logger log = LoggerFactory.getLogger(RadioRequestHandler)

    Optional<Response> say(HandlerInput input, String speechText) {
        log.debug("say: '$speechText'")
        input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Denmark's Radio", speechText)
                .withReprompt(speechText)
                .build()
    }

    Optional<Response> ask(HandlerInput input, String speechText, String reprompt) {
        log.debug("ask: '$speechText', reprompt: '$reprompt'")
        input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Denmark's Radio", speechText)
                .withReprompt(reprompt)
                .build()
    }

    Optional<Response> startPlayer(HandlerInput input, String speechText, String streamUrl, String token) {
        log.debug("startPlayer: $speechText, $streamUrl")
        input.getResponseBuilder()
                .addAudioPlayerPlayDirective(PlayBehavior.REPLACE_ALL, 200L, null, token, streamUrl)
                .withSpeech(speechText)
                .withSimpleCard("Denmark's Radio", speechText)
                .build()
    }
    Optional<Response> cancelPlayer(HandlerInput input, String speechText = null, boolean shouldEndSession = true) {
        log.debug("cancelPlayer: '$speechText', end session: $shouldEndSession")
        ResponseBuilder builder = input.getResponseBuilder()
                .addAudioPlayerClearQueueDirective(ClearBehavior.CLEAR_ALL)
                .addAudioPlayerStopDirective()
                if(speechText) {
                    builder = builder
                            .withSpeech(speechText)
                            .withSimpleCard("Denmark's Radio", speechText)
                }
                builder.withShouldEndSession(shouldEndSession).build()
    }

    String getRequestId(HandlerInput input) {
        input.request.requestId
    }

    Intent getIntent(HandlerInput input) {
        return getIntentRequest(input).getIntent()
    }

    IntentRequest getIntentRequest(HandlerInput input) {
        return input.requestEnvelope.request as IntentRequest
    }

    Map<String, Slot> getSlots(HandlerInput input) {
        getIntent(input).getSlots()
    }

    AttributesManager getAttributesManager(HandlerInput input) {
        input.getAttributesManager()
    }

    void setSessionAttribute(HandlerInput input, String key, Object value) {
        Map<String, Object> attributes = getAttributesManager(input).sessionAttributes
        attributes[key] = value
    }

    Object getSessionAttribute(HandlerInput input, String key) {
        getAttributesManager(input).sessionAttributes[key]
    }

    boolean matches(HandlerInput input, String... intents) {
        return intents.any { String intent ->
            input.matches(Predicates.intentName(intent)) || input.request.type == intent
        }
    }

    boolean matches(HandlerInput input, Class<? extends Request>... requestTypes) {
        return requestTypes.any { Class requestType ->
            input.matches(Predicates.requestType(requestType))
        }
    }
}

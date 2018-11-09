package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.exception.ExceptionHandler
import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.exception.AskSdkException
import com.amazon.ask.model.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class RadioExceptionHandler implements ExceptionHandler {
    @Override
    boolean canHandle(HandlerInput handlerInput, Throwable throwable) {
        return throwable instanceof AskSdkException
    }

    @Override
    Optional<Response> handle(HandlerInput handlerInput, Throwable throwable) {
        log.error("Alexa caught an error: $throwable.message, $handlerInput.request")
        return handlerInput.getResponseBuilder().build()
    }
}

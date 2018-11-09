package dk.glasius.alexa.dr.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.Response
import com.amazon.ask.model.Slot
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class SelectP4Handler implements RequestHandler, RadioRequestHandler {

    Properties stationList

    private final static Map P4 = [
            'bornholm'        : 'bornholm',
            'east denmark'    : 'bornholm',
            'eastern denmark' : 'bornholm',
            'esbjerg'         : 'esbjerg',
            'southern jutland': 'esbjerg',
            'funen'           : 'funen',
            'copenhagen'      : 'copenhagen',
            'midt west'       : 'midtwest',
            'north'           : 'north',
            'north jutland'   : 'north',
            'northern jutland': 'north',
            'zealand'         : 'zealand',
            'south'           : 'south',
            'triangle area'   : 'triangle',
            'tri area'        : 'triangle',
            'tri city'        : 'triangle',
            'tri city area'   : 'triangle',
            'east jutland'    : 'east',
            'east'            : 'east',
            'eastern jutland' : 'east',
    ]


    @Override
    boolean canHandle(HandlerInput input) {
        return matches(input, "SelectRegionIntent")
    }

    @Override
    Optional<Response> handle(HandlerInput input) {
        Map<String, Slot> slots = getSlots(input)

        String region = slots['region']?.value
        log.debug("Got slot 'region' with value ${region}. Now trying to get properties for region")

        String propertyName = getPropertyForRegion(region)
        if (!propertyName) {
            def response = "Could not find the right region. Please say 'region', and the region name"
            def reprompt = "Please say 'region' and the region name"
            return ask(input, response, reprompt)
        }

        String programName = stationList.getProperty("${propertyName}.name")
        String streamUrl = stationList.getProperty("${propertyName}.url")
        String streamToken = getRequestId(input) + streamUrl

        setSessionAttribute(input, PROGRAM_NAME, programName)
        setSessionAttribute(input, STREAM_URL, streamUrl)
        setSessionAttribute(input, STREAM_TOKEN, streamToken)

        startPlayer(input, "Starting '${programName}'", streamUrl, streamToken)
    }

    private static String getPropertyForRegion(String region) {
        if (P4[region]) {
            return "p4_${P4[region?.toLowerCase()]}"
        } else {
            return null
        }
    }


}

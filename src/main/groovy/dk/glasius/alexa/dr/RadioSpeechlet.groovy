package dk.glasius.alexa.dr

import com.amazon.speech.slu.Intent
import com.amazon.speech.speechlet.*
import com.amazon.speech.ui.AudioDirective
import com.amazon.speech.ui.AudioDirectivePlay
import com.amazon.speech.ui.AudioDirectiveStop
import com.amazon.speech.ui.AudioItem
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import com.amazon.speech.ui.Stream
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.util.logging.Slf4j

/**
 * This app will play the offical Denmarks Radio streams
 * @author Søren Berg Glasius
 */
@CompileStatic
@Slf4j
class RadioSpeechlet implements Speechlet {

    String title = "Denmark's Radio Streamer Unoficcial Skill"

    public static final int DEFAULT_STREAM_OFFSET = 29
    public static final String PROGRAM_NAME = 'programName'
    public static final String STREAM_URL = 'streamUrl'
    public static final String STREAM_OFFSET = 'streamOffset'

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
    void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.requestId, session.sessionId)

        initializeComponents(session)
    }

    @Override
    SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.requestId, session.sessionId)

        getWelcomeResponse(session)
    }

    @Override
    SpeechletResponse onIntent(final IntentRequest request, final Session session, final Context context)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}, intent={}", request.requestId, session.sessionId, request.intent?.name)
        Intent intent = request.intent
        String intentName = (intent != null) ? intent.getName() : null
        switch (intentName) {
            case 'SelectProgramIntent':
                playProgramResponse(request, session, context)
                break
            case 'SelectRegionIntent':
                playRegionResponse(request, session, context)
                break
            case 'SelectNewsIntent':
                playNewsResponse(request, session, context)
                break
            case "AMAZON.HelpIntent":
            case "HelpIntent":
                getHelpResponse()
                break
            case "AMAZON.CancelIntent":
                cancelPlayback()
                break
            case "AMAZON.PauseIntent":
            case "AMAZON.StopIntent":
                pausePlayback(request, session, context)
                break
            case "AMAZON.ResumeIntent":
                resumePlayback(request, session, context)
                break
            default:
                getWelcomeResponse(session)
                break
        }
    }


    @Override
    void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId())
        // any cleanup logic goes here
    }


    @Override
    SpeechletResponse onPlaybackStarted(PlaybackStartedRequest playbackStartedRequest, Context context) throws SpeechletException {
        log.debug("Playback started")
        return null
    }

    @Override
    SpeechletResponse onPlaybackFinished(PlaybackFinishedRequest playbackFinishedRequest, Context context) throws SpeechletException {
        log.debug("Playback started")
        return null
    }

    @Override
    void onPlaybackStopped(PlaybackStoppedRequest playbackStoppedRequest, Context context) throws SpeechletException {
    }

    @Override
    SpeechletResponse onPlaybackNearlyFinished(PlaybackNearlyFinishedRequest playbackNearlyFinishedRequest, Context context) throws SpeechletException {
        return null
    }

    @Override
    SpeechletResponse onPlaybackFailed(PlaybackFailedRequest playbackFailedRequest, Context context) throws SpeechletException {
        return null
    }

    @Override
    void onSystemException(SystemExceptionEncounteredRequest systemExceptionEncounteredRequest) throws SpeechletException {

    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse(Session session) {
        String speechText = "Welcome to the unofficial Denmark's Radio skill. Which station do you want to listen to?"
        askResponse(speechText, speechText)
    }

    private SpeechletResponse playProgramResponse(IntentRequest request, Session session, Context context) {
        Integer program = request.intent.getSlot("program").value?.toInteger()
        log.debug("Got slot 'program' with value ${program}")

        if (program < 0 || program > 8) {
            def noProgramResponse = "Could not find the right program. Programs are a number between 1 and 8. For a P 4 regional station, say 'region', followed by the region name"
            return askResponse(noProgramResponse, noProgramResponse)
        }

        String propertyName = "p$program"
        if (propertyName == 'p4') {
            def p4response = "To listen to a P 4 regional station, say 'region', followed by the region name"
            return askResponse(p4response, p4response)
        }

        String programName = stationList.getProperty("${propertyName}.name")
        String streamUrl = stationList.getProperty("${propertyName}.url")

        session.setAttribute(PROGRAM_NAME, programName)
        session.setAttribute(STREAM_URL, streamUrl)

        String speechText = "Now starting '${programName}'"
        return tellPlayStream(request, streamUrl, speechText)
    }

    private SpeechletResponse playRegionResponse(IntentRequest request, Session session, Context context) {
        String region = request.intent.getSlot('region')?.value
        log.debug("Got slot 'region' with value ${region}. Now trying to get properties for region")

        String propertyName = getPropertyForRegion(region)
        if (!propertyName) {
            def noProgramResponse = "Could not find the right region. Please say 'region', and the region name"
            return askResponse(noProgramResponse, noProgramResponse)
        }

        String programName = stationList.getProperty("${propertyName}.name")
        String streamUrl = stationList.getProperty("${propertyName}.url")

        session.setAttribute(PROGRAM_NAME, programName)
        session.setAttribute(STREAM_URL, streamUrl)

        String speechText = "Now starting '${programName}'"
        return tellPlayStream(request, streamUrl, speechText)
    }

    private SpeechletResponse playNewsResponse(IntentRequest request, Session session, Context context) {
        log.debug("Playing news and sports loop")
        String programName = stationList.getProperty("news.name")
        String streamUrl = stationList.getProperty("news.url")

        session.setAttribute(PROGRAM_NAME, programName)
        session.setAttribute(STREAM_URL, streamUrl)
        session.setAttribute(STREAM_OFFSET, 0)

        String speechText = "Now starting '${programName}'"
        return tellPlayStream(request, streamUrl, speechText, 0)
    }

    private SpeechletResponse pausePlayback(IntentRequest request, Session session, Context context) {
        log.debug("Cancel intent")
        String speechText = "Pausing Denmark's Radio. Say 'resume' to continue ${session.getAttribute(PROGRAM_NAME)}"
        tellCancelStream(speechText)

    }

    private SpeechletResponse resumePlayback(IntentRequest request, Session session, Context context) {
        String programName = session.getAttribute(PROGRAM_NAME)
        if(!programName) {
            String noProgramName = "Could not continue playback"
            return tellResponse(noProgramName)
        }
        log.debug("Resuming playback of $programName")
        String streamUrl = session.getAttribute(STREAM_URL)
        Integer streamOffset = session.getAttribute(STREAM_OFFSET) as Integer
        String speechText = "Resuming Denmark's Radio '${programName}'"
        return tellPlayStream(request, streamUrl, speechText, streamOffset ?: DEFAULT_STREAM_OFFSET)
    }


    private SpeechletResponse cancelPlayback() {
        log.debug("Cancel intent")
        String speechText = "Stopping Denmark's Radio. GoodBye."
        tellCancelStream(speechText)
    }

    private SpeechletResponse tellCancelStream(String speechText) {
        AudioDirectiveStop audioDirectiveClearQueue = new AudioDirectiveStop()
        //audioDirectiveClearQueue.clearBehaviour = "CLEAR_ALL"
        // Create the Simple card content.
        SimpleCard card = new SimpleCard()
        card.setTitle(title)
        card.setContent(speechText)

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.setText(speechText)

        // Create reprompt
        Reprompt reprompt = new Reprompt()
        reprompt.setOutputSpeech(speech)


        SpeechletResponse.newTellResponse(speech, card, [audioDirectiveClearQueue] as List<AudioDirective>)
    }


    private SpeechletResponse tellPlayStream(IntentRequest request, String streamUrl, String speechText, int offset = DEFAULT_STREAM_OFFSET) {
        log.debug("Trying to start ${streamUrl}")
        if (!streamUrl) {
            String noStation = "Did not find a station, please start over."
            return tellResponse(noStation)
        }

        SimpleCard card = new SimpleCard()
        card.setTitle(title)
        card.setContent(speechText)
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()

        speech.setText(speechText)

        Stream audioStream = new Stream()
        audioStream.url = streamUrl
        audioStream.setToken((request.getRequestId() + streamUrl).hashCode() as String)
        audioStream.offsetInMilliseconds = (offset * 60) * 1000 // DR buffers ~30 minutes
        AudioItem audioItem = new AudioItem(audioStream)

        AudioDirectivePlay audioPlayerPlay = new AudioDirectivePlay(audioItem)

        return SpeechletResponse.newTellResponse(speech, card, [audioPlayerPlay] as List<AudioDirective>)
    }

    private SpeechletResponse askResponse(String cardText, String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard()
        card.setTitle(title)
        card.setContent(cardText)

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.setText(speechText)

        // Create reprompt
        Reprompt reprompt = new Reprompt()
        reprompt.setOutputSpeech(speech)

        SpeechletResponse.newAskResponse(speech, reprompt, card)
    }


    private SpeechletResponse tellResponse(String cardText, String speechText = null) {
        if(!speechText) {
            speechText = cardText
        }
        // Create the Simple card content.
        SimpleCard card = new SimpleCard()
        card.setTitle(title)
        card.setContent(cardText)

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech()
        speech.setText(speechText)

        // Create reprompt
        Reprompt reprompt = new Reprompt()
        reprompt.setOutputSpeech(speech)

        SpeechletResponse.newTellResponse(speech, card)
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "To select a program say 'P' followed by the a number from 1 through 8, for example you could say 'P 1'. To listen to P 4 regional stations say 'play region', followed by the region name. To listen to the hourly news loop say 'play news'"

        askResponse(speechText, speechText)
    }


    private void initializeComponents(Session session) {
        // initialize any components here like set up a dynamodb connection
    }

    @Memoized
    private static Properties getStationList() {
        final Properties properties = new Properties()
        try {
            InputStream stream = RadioSpeechlet.class.getClassLoader() getResourceAsStream("radiostations.properties")
            properties.load(stream)
        } catch (e) {
            log.error("Unable to aws application id. Please set up a speechlet.properties")
        }
        log.debug(properties.dump())
        return properties
    }


    private static String getPropertyForRegion(String region) {
        if (P4[region]) {
            return "p4_${P4[region?.toLowerCase()]}"
        } else {
            return null
        }
    }
}

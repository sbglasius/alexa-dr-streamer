package dk.glasius.alexa.dr

import com.amazon.ask.Skill
import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills
import dk.glasius.alexa.dr.handlers.*
import dk.glasius.alexa.dr.utils.RadioSettings
import groovy.transform.CompileStatic

@CompileStatic
class RadioStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {

        Properties stationList = RadioSettings.stationList

        return Skills.standard()
                .addRequestHandlers(
                new SelectProgramHandler(stationList: stationList),
                new SelectP4Handler(stationList: stationList),
                new SelectNewsHandler(stationList: stationList),
                new PauseProgramHandler(),
                new ResumeProgramHandler(),
                new CancelProgramHandler(),
                new HelpIntentHandler(),
                new LaunchRequestHandler(),
                new SessionEndedRequestHandler(),
                new RadioAudioPlayerEventsHandler())
                .addExceptionHandler(new RadioExceptionHandler())
                .build()
    }

    RadioStreamHandler() {
        super(getSkill())
    }
}

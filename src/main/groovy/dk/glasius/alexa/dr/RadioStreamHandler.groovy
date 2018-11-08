package dk.glasius.alexa.dr

import com.amazon.ask.Skill
import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills
import dk.glasius.alexa.dr.handlers.CancelProgramHandler
import dk.glasius.alexa.dr.handlers.HelpIntentHandler
import dk.glasius.alexa.dr.handlers.LaunchRequestHandler
import dk.glasius.alexa.dr.handlers.PauseOrStopProgramHandler
import dk.glasius.alexa.dr.handlers.ResumeProgramHandler
import dk.glasius.alexa.dr.handlers.SelectNewsHandler
import dk.glasius.alexa.dr.handlers.SelectP4Handler
import dk.glasius.alexa.dr.handlers.SelectProgramHandler
import dk.glasius.alexa.dr.handlers.SessionEndedRequestHandler
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
                new PauseOrStopProgramHandler(),
                new ResumeProgramHandler(),
                new CancelProgramHandler(),
                new HelpIntentHandler(),
                new LaunchRequestHandler(),
                new SessionEndedRequestHandler())

                .build()
    }

    RadioStreamHandler() {
        super(getSkill())
    }
}

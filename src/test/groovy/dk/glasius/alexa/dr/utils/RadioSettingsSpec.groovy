package dk.glasius.alexa.dr.utils

import spock.lang.Specification

class RadioSettingsSpec extends Specification {
    def "test getting the stationList"() {
        when:
            Properties stationList = RadioSettings.stationList

        then:
            noExceptionThrown()
            stationList.size() == 36
            stationList.'news.name' == 'the News and Sports loop from Denmarks Radio'
    }
}



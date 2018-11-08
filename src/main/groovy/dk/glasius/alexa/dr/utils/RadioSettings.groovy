package dk.glasius.alexa.dr.utils

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class RadioSettings {
    @Memoized
    static Properties getStationList() {
        final Properties properties = new Properties()
        try {
            InputStream stream = RadioSettings.getResourceAsStream("/radio-stations.properties")
            properties.load(stream)
        } catch (ignored) {
            log.error("Unable to aws application id. Please set up a speechlet.properties")
        }
        properties.sort {it.key}.each { key, value ->
            log.debug("$key = $value")
        }
        return properties
    }
}

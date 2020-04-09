package logging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(
                injectionPoint.getMember().getDeclaringClass().getName());
    }
}

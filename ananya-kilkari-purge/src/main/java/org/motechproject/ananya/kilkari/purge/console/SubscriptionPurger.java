package org.motechproject.ananya.kilkari.purge.console;

import org.motechproject.ananya.kilkari.purge.exception.WrongNumberArgsException;
import org.motechproject.ananya.kilkari.purge.service.PurgeOrchestrator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class SubscriptionPurger {
    private static final String APPLICATION_CONTEXT_XML = "applicationKilkariPurgeContext.xml";

    public static void main(String[] args) throws WrongNumberArgsException, IOException {
        validateArguments(args);
        String filePath = args[0];
        PurgeOrchestrator purgeOrchestrator = getPurgeOrchestrator();
        purgeOrchestrator.purgeSubscriptionData(filePath);
    }

    private static PurgeOrchestrator getPurgeOrchestrator() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        context.getEnvironment().setActiveProfiles("production");
        context.setConfigLocation(APPLICATION_CONTEXT_XML);
        context.refresh();
        return (PurgeOrchestrator) context.getBean("purgeOrchestrator");
    }

    private static void validateArguments(String[] args) throws WrongNumberArgsException {
        if (args.length != 1)
            throw new WrongNumberArgsException("Wrong number of arguments. Arguments expected: <file_name>");
    }
}

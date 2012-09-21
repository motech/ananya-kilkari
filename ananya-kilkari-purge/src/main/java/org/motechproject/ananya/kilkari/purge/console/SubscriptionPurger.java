package org.motechproject.ananya.kilkari.purge.console;

import org.motechproject.ananya.kilkari.purge.service.PurgeOrchestrator;
import org.motechproject.ananya.kilkari.purge.exception.WrongNumberArgsException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class SubscriptionPurger {
    private static final String APPLICATION_CONTEXT_XML = "applicationKilkariPurgeContext.xml";

    public static void main(String[] args) throws WrongNumberArgsException, IOException {
        validateArguments(args);
        String filePath = args[0];
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        PurgeOrchestrator purgeOrchestrator = (PurgeOrchestrator) context.getBean("purgeOrchestrator");
        purgeOrchestrator.purgeSubscriptionData(filePath);
    }

    private static void validateArguments(String[] args) throws WrongNumberArgsException {
        if (args.length != 1)
            throw new WrongNumberArgsException("Wrong number of arguments. Arguments expected : <file_name>");
    }
}

import org.apache.log4j.Logger;

/**
 * CheckModule
 * Created by ccwei on 2018/11/19.
 */
public class TestLog {

    private static Logger logger = Logger.getLogger(TestLog.class.getName());

    public static void main(String args[])
    {
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.fatal("fatal");
    }

}

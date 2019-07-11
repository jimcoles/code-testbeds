package org.jkcsoft.jasmin.test;

import org.jkcsoft.jasmin.services.userdb.UserMongoDbService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jim Coles
 */

public class UserTests {

    private static Logger log = LoggerFactory.getLogger(UserMongoDbService.class);

    @Test
    public void testMongoDb() {
        UserMongoDbService service = new UserMongoDbService(null, null);
        service.getStatus();;
        log.info("test complete");
    }
}

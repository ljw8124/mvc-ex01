package org.zerock.chatting.dao;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class TimeDAOTests {

    @Test
    public void testGetTime() {
        log.info("testGetTime.........");
        String timeMsg = TimeDAO.INSTANCE.getTime();

        log.info(timeMsg);
    }

}

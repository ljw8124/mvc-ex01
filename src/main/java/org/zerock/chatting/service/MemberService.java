package org.zerock.chatting.service;

import org.zerock.chatting.dao.MemberDAO;
import org.zerock.chatting.dto.MemberDTO;

public enum MemberService {

    INSTANCE;

    public MemberDTO login(String mid, String mpw) {

        MemberDTO resultDto = MemberDAO.INSTANCE.login(mid, mpw);

        return resultDto;
    }
}

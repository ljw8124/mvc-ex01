package org.zerock.chatting.dao;

import lombok.extern.log4j.Log4j2;
import org.zerock.chatting.dto.MsgDTO;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public enum MsgDAO {
    INSTANCE;

    private static final String SQL_INSERT = "insert into tbl_msg (who,whom,content) values (?,?,?)";
    private static final String SQL_LIST = "select mno, who, whom, if (whom = ?, 'R', 'S') kind, content, regdate, opendate\n" +
            "from\n" +
            "tbl_msg\n" +
            "where\n" +
            "whom = ? or who = ?\n" +
            "order by kind asc, mno desc";

    private static final String SQL_SELECT = "select mno,who,whom,content,regdate,opendate from tbl_msg where mno = ?";
    private static final String SQL_UPDATE_OPEN = "update tbl_msg set opendate = now() where mno = ?";

    private static final String SQL_REMOVE = "delete from tbl_msg where mno = ? and who = ?";


    public void insert(MsgDTO msgDTO) throws RuntimeException {

        new JdbcTemplate() {

            @Override
            protected void execute() throws Exception {
                //who,whom,content
                int idx = 1;
                preparedStatement = connection.prepareStatement(SQL_INSERT);
                preparedStatement.setString(idx++, msgDTO.getWho());
                preparedStatement.setString(idx++, msgDTO.getWhom());
                preparedStatement.setString(idx++, msgDTO.getContent());

                int count = preparedStatement.executeUpdate();
                log.info("count = " + count);

            }
        }.makeAll();

    }

    public Map<String, List<MsgDTO>> selectList(String user) throws RuntimeException {
        Map<String, List<MsgDTO>> listMap = new HashMap<>();
        listMap.put("R", new ArrayList<>());
        listMap.put("S", new ArrayList<>());

        new JdbcTemplate() {
            @Override
            protected void execute() throws Exception {
                preparedStatement = connection.prepareStatement(SQL_LIST);
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, user);
                preparedStatement.setString(3, user);

                resultSet = preparedStatement.executeQuery();

                log.info(resultSet);
                while (resultSet.next()) {

                    String kind = resultSet.getString(4);

                    List<MsgDTO> targetList = listMap.get(kind);
                    // mno, who, whom, if(whom = ?, 'R', 'S') kind, content,
                    // regdate, opendate
                    targetList.add(MsgDTO.builder()
                            .mno(resultSet.getLong(1))
                            .who(resultSet.getString(2))
                            .whom(resultSet.getString(3))
                            .content(resultSet.getString(5))
                            .regdate(resultSet.getTimestamp(6))

                            .opendate(resultSet.getTimestamp(7))
                            .build());
                }
            }
        }.makeAll();

        return listMap;
    }

    public MsgDTO read(long mno) throws RuntimeException {

        MsgDTO msgDTO = MsgDTO.builder().build();

        new JdbcTemplate() {

            @Override
            protected void execute() throws Exception {
                preparedStatement = connection.prepareStatement(SQL_UPDATE_OPEN);
                preparedStatement.setLong(1,mno);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                preparedStatement = null; //GC로 메모리를 빨리 회수하기 위해서 null;을 넣음

                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setLong(1,mno);

                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                //mno,who,whom,content,regdate
                //opendate
                msgDTO.setMno(resultSet.getLong(1));
                msgDTO.setWho(resultSet.getString(2));
                msgDTO.setWhom(resultSet.getString(3));
                msgDTO.setContent(resultSet.getString(4));
                msgDTO.setRegdate(resultSet.getTimestamp(5));

                msgDTO.setRegdate(resultSet.getTimestamp(6));
            }
        }.makeAll();

        return msgDTO;
    }

    public MsgDTO remove(long mno, String who) throws RuntimeException {

        MsgDTO msgDTO = MsgDTO.builder().build();

        new JdbcTemplate() {
            @Override
            protected void execute() throws Exception {
                //mno,who

                preparedStatement = connection.prepareStatement(SQL_REMOVE);
                preparedStatement.setLong(1, mno);
                preparedStatement.setString(2, who);
                preparedStatement.executeUpdate();


//                msgDTO.setMno(resultSet.getLong(1));
//                msgDTO.setWho(resultSet.getString(2));

            }
        }.makeAll();

        return msgDTO;
    }

}



package org.zerock.chatting.dao;

import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//안정적인 jdbc연결을 위해서 만듦
@Log4j2
abstract class JdbcTemplate {

    static{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected ResultSet resultSet;


    //템플릿 생성 연결후 기능 -> finally에서 close()
    public void makeAll() throws RuntimeException {
        try {
            makeConnection();
            execute();
            log.info("END");
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            finish(); //닫는 코드, 보통 닫는 것은 finally에 있어야함.
        }
    }

    protected abstract void execute() throws Exception;

    private void makeConnection() throws Exception{
        log.info("Connection......");
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bit08db","bituser","bituser");
    }

    private void finish() {
        log.info("------------finish--------------");
        log.info("Template ResultSet: "+ resultSet);
        log.info("Template PreparedStatement: "+preparedStatement);
        log.info(connection);

        if (resultSet != null) {
            try{ resultSet.close(); } catch(Exception e){}
        }

        if (preparedStatement != null){
            try{ preparedStatement.close(); } catch(Exception e){}
        }

        if (connection != null) {
            try{ connection.close(); } catch(Exception e){}
        }
    }
}

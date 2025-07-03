package com.mark.jdbc.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;

class DBConnectionUtilTest {

    @Test
    void getConnection(){
        Connection conn = DBConnectionUtil.getConnection();
        assertThat(conn).isNotNull();
    }

}
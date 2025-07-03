package com.mark.jdbc.service;

import com.mark.jdbc.domain.Member;
import com.mark.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 repository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);
            bizLogic(fromId, toId, money, conn);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }
    }

    private void bizLogic(String fromId, String toId, int money, Connection conn) throws SQLException {
        Member fromMember = repository.findById(conn, fromId);
        Member toMember = repository.findById(conn, toId);
        repository.update(conn, fromId, fromMember.getMoney() - money);
        validation(toMember);
        repository.update(conn, toId, toMember.getMoney() + money);
    }

    private static void release(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                log.info("error", e);
            }

        }
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) throw new IllegalStateException("이체중 예외 발생");
    }
}

package com.mark.jdbc.service;

import com.mark.jdbc.connection.ConnectionConst;
import com.mark.jdbc.domain.Member;
import com.mark.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.mark.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceV1Test {

    private final static String MEMBER_A = "memberA";
    private final static String MEMBER_B = "memberB";
    private final static String MEMBER_EX = "ex";

    private MemberRepositoryV1 repository;
    private MemberServiceV1 service;

    @BeforeEach
    void beforeEach(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
        service = new MemberServiceV1(repository);
    }

    @AfterEach()
    void afterEach() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);

        repository.save(memberA);
        repository.save(memberB);

        //when
        service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member fromMember = repository.findById(memberA.getMemberId());
        Member toMember = repository.findById(memberB.getMemberId());

        assertThat(fromMember.getMoney()).isEqualTo(8000);
        assertThat(toMember.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);

        repository.save(memberA);
        repository.save(memberB);

        //when
        assertThatThrownBy(() -> service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member fromMember = repository.findById(memberA.getMemberId());
        Member toMember = repository.findById(memberB.getMemberId());

        //memberA의 돈만 2000원 줄었고, ex의 돈은 10000원 그대로이다.
        assertThat(fromMember.getMoney()).isEqualTo(8000);
        assertThat(toMember.getMoney()).isEqualTo(10000);
    }


}
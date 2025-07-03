package com.mark.jdbc.service;

import com.mark.jdbc.domain.Member;
import com.mark.jdbc.repository.MemberRepository;
import com.mark.jdbc.repository.MemberRepositoryV4_1;
import com.mark.jdbc.repository.MemberRepositoryV4_2;
import com.mark.jdbc.repository.MemberRepositoryV5;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
*
* 예외 누수 문제 해결
* SQLException 제거
* MemberRepository 인터페이스 의존
*/
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    private final static String MEMBER_A = "memberA";
    private final static String MEMBER_B = "memberB";
    private final static String MEMBER_EX = "ex";

    @Autowired
    private MemberServiceV4 memberService;
    @Autowired
    private MemberRepository memberRepository;

    @TestConfiguration
    static class TestConfig{

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository(){
            //return new MemberRepositoryV4_1(dataSource);
            //return new MemberRepositoryV4_2(dataSource);
            return new MemberRepositoryV5(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4(){
            return new MemberServiceV4(memberRepository());
        }

    }

    @AfterEach()
    void afterEach() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member fromMember = memberRepository.findById(memberA.getMemberId());
        Member toMember = memberRepository.findById(memberB.getMemberId());

        assertThat(fromMember.getMoney()).isEqualTo(8000);
        assertThat(toMember.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외발생")
    void accountTransferEx() {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member fromMember = memberRepository.findById(memberA.getMemberId());
        Member toMember = memberRepository.findById(memberB.getMemberId());

        //memberA의 돈이 롤백 되어야함
        assertThat(fromMember.getMoney()).isEqualTo(10000);
        assertThat(toMember.getMoney()).isEqualTo(10000);
    }

    @Test
    void aopCheck(){
        log.info("memberService={}", memberService.getClass());
        log.info("memberRepository={}", memberRepository.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

}
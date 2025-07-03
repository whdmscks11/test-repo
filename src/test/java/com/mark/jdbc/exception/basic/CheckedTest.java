package com.mark.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    void checkedCatch(){
        Service service = new Service();
        service.callCatch();
        // 정상 흐름
    }

    @Test
    void checkedThrow(){
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception을 상속 받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception{
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * 체크 예외는 잡아서 처리하거나, 던지거나 둘 중 하를 필수로 선택해야 한다.
     */
    static class Service{

        private final Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}",e.getMessage(), e);
            }
        }

        /**
         * 예외를 밖으로 던지는 코드
         * 예외를 던지는 경우, throws 키워드 필수!
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException{
            repository.call();
        }
    }

    static class Repository{

        public void call() throws MyCheckedException{
            throw new MyCheckedException("ex");
        }
    }
}

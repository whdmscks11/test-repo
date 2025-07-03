package com.mark.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {

    @Test
    void uncheckedCatch(){
        Service service = new Service();
        service.callCatch();
        // 정상 흐름
    }

    @Test
    void uncheckedThrow(){
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException을 상속 받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 언체크 예외는 잡아서 처리하거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service{

        private final Repository repository = new Repository();

        /**
         * 필요한 경우 언체크 예외도 잡아서 처리할 수 있다.
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}",e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 되며, 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 예외를 던질 때 throws 키워드를 선언하지 않아도 된다.
         * @throws MyUncheckedException
         */
        public void callThrow() throws MyUncheckedException{
            repository.call();
        }
    }

    static class Repository{

        public void call() throws MyUncheckedException{
            throw new MyUncheckedException("ex");
        }
    }
}

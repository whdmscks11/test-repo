package com.mark.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class CheckedAppTest {

    @Test
    void checked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    static class Controller{
        private final Service service = new Service();
        public void request() throws SQLException, ConnectException {
            service.logic();
        }

    }
    static class Service{

        private final Repository repository = new Repository();
        private final NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }
    static class Repository{
        public void call() throws SQLException {
            throw new SQLException();
        }
    }
    static class NetworkClient{
        public void call() throws ConnectException {
            throw new ConnectException();
        }
    }
}

package ru.netology.data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBUtils {
    private static String url = System.getProperty("db.url");
    private static String userDB = System.getProperty("app.userDB");
    private static String password = System.getProperty("app.password");

    @SneakyThrows
    public static void clearAllData() {
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection(url, userDB, password);
        runner.update(conn, "DELETE FROM credit_request_entity;");
        runner.update(conn, "DELETE FROM payment_entity;");
        runner.update(conn, "DELETE FROM order_entity;");
    }

    @SneakyThrows
    public static void checkPaymentStatus(Status status) {
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection(url, userDB, password);
        val paymentDataSQL = "SELECT status FROM payment_entity;";
        val payment = runner.query(conn, paymentDataSQL, new BeanHandler<>(Payment.class));
        assertEquals(status, payment.status);
    }

    @SneakyThrows
    public static void checkCreditStatus(Status status) {
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection(url, userDB, password);
        val creditDataSQL = "SELECT status FROM credit_request_entity;";
        val credit = runner.query(conn, creditDataSQL, new BeanHandler<>(Credit.class));
        assertEquals(status, credit.status);
    }

    @SneakyThrows
    public static int checkEntityCount() {
        val creditEntityRq = "SELECT * FROM credit_request_entity;";
        val orderEntityRq = "SELECT * FROM order_entity;";
        val payEntityRq = "SELECT * FROM payment_entity;";
        int countEntity = 0;
        try (
                val conn = DriverManager.getConnection(url, userDB, password);
                Statement statement = conn.createStatement();
        ) {
            try (ResultSet resultSet = statement.executeQuery(creditEntityRq)) {
                if (resultSet.next()) {
                    countEntity = countEntity + 1;
                }
            }
            try (ResultSet resultSet = statement.executeQuery(orderEntityRq)) {
                if (resultSet.next()) {
                    countEntity = countEntity + 2;
                }
            }
            try (ResultSet resultSet = statement.executeQuery(payEntityRq)) {
                if (resultSet.next()) {
                    countEntity = countEntity + 3;
                }
            }
        }
        return countEntity;
    }

    @SneakyThrows
    public static int getPayAmount() {
        val payStatusRq = "SELECT * FROM payment_entity;";
        try (
                val conn = DriverManager.getConnection(url, userDB, password);
                Statement statement = conn.createStatement();
        ) {
            try (ResultSet resultSet = statement.executeQuery(payStatusRq)) {
                if (resultSet.next()) {
                    return resultSet.getInt("amount");
                }
            }
        }
        return 0;
    }
}



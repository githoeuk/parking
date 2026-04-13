
package com.tenco.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/parking");
        config.setUsername(System.getenv("DB_USER"));
        config.setPassword(System.getenv("DB_PASSWORD"));

        config.setMaximumPoolSize(10);       // 동시에 유지할 연결 최대 개수
        config.setMinimumIdle(3);            // 확보할 최소 유후 커넥션 수
        config.setConnectionTimeout(10000);  // 커넥션 할당 대기 시간 제한 (10초)
        config.setIdleTimeout(600000);       // 유후 커넥션 유지 시간(30초)

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if(dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }
}

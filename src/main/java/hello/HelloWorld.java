package hello;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;

// 程序入口
@SpringBootApplication
public class HelloWorld {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HelloWorld.class, args);
    }

    @Autowired
    private Environment env;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setInitialSize(2);  // 初始化时物理连接个数
        dataSource.setMaxActive(20);  // 最大连接池数量
        dataSource.setMinIdle(0);  // 最小连接池数量
        dataSource.setMaxWait(60000);  // 获取连接时最大等待时间
        dataSource.setValidationQuery("select 1");  // 用来检测是否连接
        dataSource.setTestOnBorrow(false);  // 申请连接时执行validationQuery检测连接是否有效
        dataSource.setTestWhileIdle(true);  // 保证安全
        dataSource.setPoolPreparedStatements(false);  // 是否缓存preparedStatement，也就是PSCache
        return dataSource;
    }
}

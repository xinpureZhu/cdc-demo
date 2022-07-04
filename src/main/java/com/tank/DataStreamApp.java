package com.tank;


import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author tank
 */
public class DataStreamApp {
  @SneakyThrows
  public static void main(@NonNull String[] args) {

    val env = StreamExecutionEnvironment.getExecutionEnvironment();
    val settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
    val tableEnvironment = StreamTableEnvironment.create(env, settings);

    val sourceTabMembersSql = StrUtil.format("create table  tab_members_v1 (`id` BIGINT primary key  NOT ENFORCED , name String ,birthDay String ) {}",
        withTableInfo("127.0.0.1", "k8s_demo_v1", "root", "123", "tab_members"));

    tableEnvironment.executeSql(sourceTabMembersSql);

    val targetTabMembersSql = "create table tab_members_v2 (`id` BIGINT  primary key  NOT ENFORCED,name String ,birthDay String ) with ('connector' = 'print')";
    tableEnvironment.executeSql(targetTabMembersSql);

    val transformSql = "insert into tab_members_v2(id, name, birthDay) select id,name,birthDay from tab_members_v1";

    val result = tableEnvironment.executeSql(transformSql);
    result.print();

    env.execute("cdc-demo");
  }

  private static String withTableInfo(@NonNull final String mysqlHosts,
                                      @NonNull final String dbName,
                                      @NonNull final String username,
                                      @NonNull final String password,
                                      @NonNull final String tableName) {
    val connector = StrUtil.format(" 'connector' = '{}'", "mysql-cdc");
    val hostname = StrUtil.format("'hostname' = '{}'", mysqlHosts);
    val port = StrUtil.format("'port'='{}'", 3306);
    val user = StrUtil.format("'username' = '{}'", username);
    val passwd = StrUtil.format("'password' = '{}'", password);
    val db = StrUtil.format("'database-name' = '{}'", dbName);
    val table = StrUtil.format("'table-name' = '{}'", tableName);
    val mode = StrUtil.format("'debezium.snapshot.mode' = '{}'", "initial");
    return StrUtil.format(" with ({})", String.join(",", connector, hostname, port, user, passwd, db, table));
  }

}


/**
 * CREATE TABLE sbtest1 (
 * id INT,
 * k INT,
 * c STRING,
 * pad STRING
 * ) WITH (
 * 'connector' = 'mysql-cdc',
 * 'hostname' = '197.XXX.XXX.XXX',
 * 'port' = '3306',
 * 'username' = 'debezium',
 * 'password' = 'PASSWORD',
 * 'database-name' = 'cdcdb',
 * 'table-name' = 'sbtest1',
 * 'debezium.snapshot.mode' = 'initial'
 * );
 */


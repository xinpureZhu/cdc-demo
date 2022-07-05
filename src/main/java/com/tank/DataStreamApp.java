package com.tank;


import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.tank.model.SourceModel;
import com.tank.model.TargetModel;
import com.tank.util.ModelUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @author tank
 */
public class DataStreamApp {
  @SneakyThrows
  public static void main(@NonNull String[] args) {

    val sourceModel = new ModelUtil("source.yml").convertTo(SourceModel.class);
    val sourceModelSource = sourceModel.getSource();
    sourceModelSource.setUsername(SystemPropsUtil.get("sourceDbUsername"));
    sourceModelSource.setPassword(SystemPropsUtil.get("sourcePassword"));
    sourceModelSource.setDbName(SystemPropsUtil.get("sourceDbName"));

    Console.log("source db info:[{}]", sourceModel.toString());

    val targetModel = new ModelUtil("source.yml").convertTo(TargetModel.class);
    val targetModelSource = targetModel.getSource();
    targetModelSource.setUsername(SystemPropsUtil.get("targetDbUsername"));
    targetModelSource.setPassword(SystemPropsUtil.get("targetPassword"));
    targetModelSource.setDbName(SystemPropsUtil.get("targetDbName"));

    Console.log("targetModel db info:[{}]", targetModel.toString());

    val env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);
    val settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
    val tableEnv = StreamTableEnvironment.create(env, settings);

    val sourceTabMembersSql = StrUtil.format("create table  tab_members_v1 (`id` BIGINT primary key  NOT ENFORCED , name String ,birthDay String ) {}",
        withTableInfo("mysql", "k8s_demo_v1", "root", "123", "tab_members"));

    tableEnv.executeSql(sourceTabMembersSql);

    val targetTabMembersSql = "create table tab_members_v2 (`id` BIGINT primary key  NOT ENFORCED , name String ,birthDay String ) WITH (" +
        "    'connector' = 'jdbc'," +
        "    'url' = 'jdbc:mysql://mysql:3306/k8s_demo_v2',\n" +
        "    'username'= 'root'," +
        "    'password'= '123'," +
        "    'table-name' = 'tab_members'" +
        ")";

    tableEnv.executeSql(targetTabMembersSql);


    val result = tableEnv.sqlQuery("select id, name,  birthDay from tab_members_v1");
    result.select($("id"), $("name"), $("birthDay")).executeInsert("tab_members_v2", false);

    val retractStream = tableEnv.toRetractStream(result, Row.class);
    retractStream.print();

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



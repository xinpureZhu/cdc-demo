package com.tank;


import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.LocalDateTime;

/**
 * @author tank
 */
public class DataStreamApp {
  @SneakyThrows
  public static void main(String[] args) {
    DebeziumSourceFunction<String> source = MySqlSource.<String>builder()
        .hostname("tank")
        .port(3306)
        .username("root")
        .password("123")
        .databaseList("customer_db")
        .tableList("customer_db.tab_member_score")
        .startupOptions(StartupOptions.latest())
        .deserializer(new StringDebeziumDeserializationSchema())
        .build();

    val env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.enableCheckpointing(3000).setParallelism(1);

    env.addSource(source).print();

    env.execute("cdc-demo");
  }

  @Getter
  @Setter
  @Accessors(chain = true)
  private static class MemberScore implements Comparable<MemberScore> {

    private String memberId;
    private Long score;
    private LocalDateTime updateTime;

    @Override
    public int compareTo(MemberScore memberScore) {
      return memberScore.getUpdateTime().isAfter(memberScore.getUpdateTime()) ? 1 :
          memberScore.getUpdateTime().isEqual(memberScore.getUpdateTime()) ? 0 : -1;
    }
  }
}

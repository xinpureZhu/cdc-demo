package com.tank;


import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author tank
 */
public class DataStreamApp {
  @SneakyThrows
  public static void main(String[] args) {
    var source = MySqlSource.<String>builder()
        .connectTimeout(Duration.ofSeconds(3L))
        .hostname("tank")
        .port(3306)
        .username("root")
        .password("123")
        .databaseList("customer_db")
        .tableList("tab_customers")
        .deserializer(new JsonDebeziumDeserializationSchema())
        .build();

    var env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.enableCheckpointing(3000);
    var dataStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "member-score-cdc");

    dataStream.map(new MapFunction<String, String>() {

      @Override
      public String map(String s) throws Exception {
        return s;
      }
    }).print();
    env.execute("");
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

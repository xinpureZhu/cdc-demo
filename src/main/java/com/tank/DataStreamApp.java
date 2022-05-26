package com.tank;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tank.model.DebeziumSourceModel;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.LocalDateTime;

/**
 * @author tank
 */
public class DataStreamApp {
  @SneakyThrows
  public static void main(String[] args) {
    val mySqlSourceBuilder = new MySqlSourceBuilder<String>();
    val source = mySqlSourceBuilder
        .hostname("tank")
        .includeSchemaChanges(true)
        .port(3306)
        .username("root")
        .password("123")
        .databaseList("customer_db")
        .tableList("customer_db.tab_member_score")
        .startupOptions(StartupOptions.latest())
        .deserializer(new JsonDebeziumDeserializationSchema())
        .build();


    val env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.fromSource(source, WatermarkStrategy.noWatermarks(), "cdc-demo").setParallelism(1)
        .map((MapFunction<String, DebeziumSourceModel>) json -> JSONUtil.toBean(json, DebeziumSourceModel.class))
        .map(model -> {
          val dto = model.getAfter();
          val memberScore = new MemberScore();
          memberScore.setScore(dto.getScore());
          memberScore.setMemberId(dto.getMemberId());
          memberScore.setUpdateTime(DateUtil.date().toLocalDateTime());
          return memberScore;
        })
        .map(MemberScore::toString)
        .print("console").setParallelism(1);

    env.execute("cdc-demo");
  }


  @Getter
  @Setter
  @Accessors(chain = true)
  private static class MemberScore implements Comparable<MemberScore> {

    @JsonProperty("member_id")
    private String memberId;
    private Integer score;
    private LocalDateTime updateTime;

    @Override
    public int compareTo(MemberScore memberScore) {
      return memberScore.getUpdateTime().isAfter(memberScore.getUpdateTime()) ? 1 :
          memberScore.getUpdateTime().isEqual(memberScore.getUpdateTime()) ? 0 : -1;
    }

    @Override
    public String toString() {
      return "MemberScore{" +
          "memberId='" + memberId + '\'' +
          ", score=" + score +
          ", updateTime=" + updateTime +
          '}';
    }
  }
}


/**
 * {
 * "before": {
 * "id": 1,
 * "member_id": "jack",
 * "score": 19985
 * },
 * "after": {
 * "id": 1,
 * "member_id": "jack",
 * "score": 19984
 * },
 * "source": {
 * "version": "1.5.4.Final",
 * "connector": "mysql",
 * "name": "mysql_binlog_source",
 * "ts_ms": 1653498663000,
 * "snapshot": "false",
 * "db": "customer_db",
 * "sequence": null,
 * "table": "tab_member_score",
 * "server_id": 1,
 * "gtid": null,
 * "file": "binlog.000045",
 * "pos": 10191,
 * "row": 0,
 * "thread": null,
 * "query": null
 * },
 * "op": "u",
 * "ts_ms": 1653498663591,
 * "transaction": null
 * }
 */

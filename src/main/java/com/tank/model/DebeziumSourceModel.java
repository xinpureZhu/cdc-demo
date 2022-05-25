package com.tank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tank
 */
@NoArgsConstructor
@Getter
@Setter
public class DebeziumSourceModel {
  @JsonProperty("before")
  private BeforeDTO before;
  @JsonProperty("after")
  private AfterDTO after;
  @JsonProperty("source")
  private SourceDTO source;
  @JsonProperty("op")
  private String op;
  @JsonProperty("ts_ms")
  private Long tsMs;
  @JsonProperty("transaction")
  private Object transaction;

  @NoArgsConstructor
  @Data
  public static class BeforeDTO {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("member_id")
    private String memberId;
    @JsonProperty("score")
    private Integer score;
  }

  @NoArgsConstructor
  @Data
  public static class AfterDTO {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("member_id")
    private String memberId;
    @JsonProperty("score")
    private Integer score;
  }

  @NoArgsConstructor
  @Data
  public static class SourceDTO {
    @JsonProperty("version")
    private String version;
    @JsonProperty("connector")
    private String connector;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ts_ms")
    private Long tsMs;
    @JsonProperty("snapshot")
    private String snapshot;
    @JsonProperty("db")
    private String db;
    @JsonProperty("sequence")
    private Object sequence;
    @JsonProperty("table")
    private String table;
    @JsonProperty("server_id")
    private Integer serverId;
    @JsonProperty("gtid")
    private Object gtid;
    @JsonProperty("file")
    private String file;
    @JsonProperty("pos")
    private Integer pos;
    @JsonProperty("row")
    private Integer row;
    @JsonProperty("thread")
    private Object thread;
    @JsonProperty("query")
    private Object query;
  }
}

package com.tank.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author tank198435163.com
 */
@Getter
@Setter
@ToString
public class DbModel {

  private String dbName;

  private String password;

  private String username;

  private List<String> tables = Lists.newArrayList();
}
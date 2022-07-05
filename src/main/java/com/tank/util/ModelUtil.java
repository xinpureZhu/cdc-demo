package com.tank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;

/**
 * @author tank198435163.com
 */
public class ModelUtil {

  public ModelUtil(@NonNull final String ymlName) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    this.file = new File(classLoader.getResource(ymlName).getFile());
    this.om = new ObjectMapper(new YAMLFactory());
  }

  @SneakyThrows
  public <T> T convertTo(@NonNull final Class<T> clazz) {
    return this.om.readValue(this.file, clazz);
  }

  private ObjectMapper om = null;

  private File file;


}

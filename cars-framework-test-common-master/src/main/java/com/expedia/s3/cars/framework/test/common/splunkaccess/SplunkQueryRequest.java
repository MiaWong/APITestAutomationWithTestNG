package com.expedia.s3.cars.framework.test.common.splunkaccess;

/**
 * Created by alex on 3/15/2017.
 */
public class SplunkQueryRequest {

  private String splunkServer;
  private int port;
  private String username;
  private String password;
  private String rawQuery;
  private int maxTryCount = 3;
  private int timeoutInSeconds = 60;
  private int tryIntervalInSeconds = 60;
  private boolean isRawQuery = true;

  public String getSplunkServer() {
    return splunkServer;
  }

  public void setSplunkServer(String splunkServer) {
    this.splunkServer = splunkServer;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRawQuery() {
    return rawQuery;
  }

  public void setRawQuery(boolean isRawQuery) {
    this.isRawQuery = isRawQuery;
  }

  public void setRawQuery(String rawQuery) {
    this.rawQuery = rawQuery;
  }

  public int getMaxTryCount() {
    return maxTryCount;
  }

  public void setMaxTryCount(int maxTryCount) {
    this.maxTryCount = maxTryCount;
  }

  public int getTimeoutInSeconds() {
    return timeoutInSeconds;
  }

  public void setTimeoutInSeconds(int timeoutInSeconds) {
    this.timeoutInSeconds = timeoutInSeconds;
  }

  public void setIsRawQuery(boolean isRawQuery) { this.isRawQuery = isRawQuery; }

  public boolean isIsRawQuery() {
    return isRawQuery;
  }

  public int getTryIntervalInSeconds() {
    return tryIntervalInSeconds;
  }

  public void setTryIntervalInSeconds(int tryIntervalInSeconds) {
    this.tryIntervalInSeconds = tryIntervalInSeconds;
  }

  @Override
  public String toString() {
    return "SplunkQueryRequest [splunkServer=" + splunkServer + ", port=" + port + ", username="
            + username + ", rawQuery=" + rawQuery + ", maxTryCount=" + maxTryCount
            + ", timeoutInSeconds=" + timeoutInSeconds + "]";
  }

}

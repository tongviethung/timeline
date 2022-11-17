package com.vpbanks.timeline.filter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ApiSecretDto.
 */
public class ApiSecretDto {

  /** The x api key. */
  @JsonProperty("x-api-key")
  private String xApiKey;

  /**
   * Gets the x api key.
   *
   * @return the xApiKey
   */
  public String getxApiKey() {
    return xApiKey;
  }

  /**
   * Sets the x api key.
   *
   * @param xApiKey the xApiKey to set
   */
  public void setxApiKey(String xApiKey) {
    this.xApiKey = xApiKey;
  }

  /**
   * Gets the x secret key.
   *
   * @return the xSecretKey
   */
  public String getxSecretKey() {
    return xSecretKey;
  }

  /**
   * Sets the x secret key.
   *
   * @param xSecretKey the xSecretKey to set
   */
  public void setxSecretKey(String xSecretKey) {
    this.xSecretKey = xSecretKey;
  }


  /** The x secret key. */
  @JsonProperty("x-api-secret")
  private String xSecretKey;


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((xApiKey == null) ? 0 : xApiKey.hashCode());
    result = prime * result + ((xSecretKey == null) ? 0 : xSecretKey.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ApiSecretDto other = (ApiSecretDto) obj;

    if (xApiKey == null) {
      if (other.xApiKey != null) {
        return false;
      }
    } else if (!xApiKey.equals(other.xApiKey)) {
      return false;
    }
    if (xSecretKey == null) {
      if (other.xSecretKey != null) {
        return false;
      }
    } else if (!xSecretKey.equals(other.xSecretKey)) {
      return false;
    }
    return true;
  }


}

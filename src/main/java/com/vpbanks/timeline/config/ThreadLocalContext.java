package com.vpbanks.timeline.config;

import com.vpbanks.timeline.util.security.SecurityUtils;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.ThreadContext;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ThreadLocalContext {

  public static void setUsername(String username) {
    ThreadContext.put(SecurityUtils.USER_NAME, username);
  }

  public static String getUsername() {
    String value = ThreadContext.get(SecurityUtils.USER_NAME);

    if (value == null) {
      return "ADMIN";
    }
    return value;
  }
}

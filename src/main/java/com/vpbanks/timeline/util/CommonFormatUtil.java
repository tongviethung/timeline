package com.vpbanks.timeline.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.lang.NonNull;

/**
 * The Class CommonFormatUtil.
 */
public class CommonFormatUtil {

  /**
   * Instantiates a new common format util.
   */
  private CommonFormatUtil() {

  }

  public static String redact(@NonNull String responseString, List<String> redactKeys) {
    final Pattern pattern = Pattern.compile("\".*?\":");
    Matcher matcher = pattern.matcher(responseString);
    ArrayList<String> listJsonKeys = new ArrayList<>();

    while (matcher.find()) {
      String container = matcher.group(0);
      String result = getJsonKey(container);
      for (String key : redactKeys) {
        if (result.contains(key)) {
          listJsonKeys.add(result);
          break;
        }
      }
    }

    responseString = handleListJsonKey(responseString, listJsonKeys);
    return responseString;
  }

  private static String getJsonKey(String request) {
    final Pattern pattern = Pattern.compile("(?<=\")(.*?)(?=\")");
    Matcher matcher = pattern.matcher(request);
    String result = "";
    while (matcher.find()) {
      result = matcher.group(0);
    }
    return result;
  }

  private static String handleListJsonKey(@NonNull String responseString, ArrayList<String> listJsonKeys) {
    StringBuilder stringBuilder = new StringBuilder(responseString);
    String regex = "(?<=\"%s\":)(.*?)(?=,|]|})";
    Matcher matcher;
    int start;
    int finish;

    for (String jsonKey : listJsonKeys) {
      matcher = Pattern.compile(String.format(regex, jsonKey)).matcher(stringBuilder);
      try {
        while (matcher.find()) {
          start = matcher.start(1);
          finish = matcher.end(1);
          stringBuilder.delete(start, finish);
          if (finish <= stringBuilder.length()) {
            matcher.region(finish, stringBuilder.length());
          }
          stringBuilder.insert(start, "\"" + "*******" + "\"");
        }
      } catch (Exception ex) {
        return "";
      }
    }
    return stringBuilder.toString();
  }
}

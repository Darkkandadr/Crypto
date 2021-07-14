package tr.com.infumia.cryptobot.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Sorter {

  public static HashMap<Object, Object> sortByValue(final HashMap<String, Object> map){
    return map.entrySet().stream()
      .sorted(Comparator.comparingDouble(e -> -(double) e.getValue()))
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (a, b) -> { throw new AssertionError();},
        LinkedHashMap::new
      ));
  }

  public static HashMap<String, Double> sortByValueDouble(final HashMap<String, Double> map){
    return map.entrySet().stream()
      .sorted(Comparator.comparingDouble(e -> -(double) e.getValue()))
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (a, b) -> { throw new AssertionError();},
        LinkedHashMap::new
      ));
  }

}

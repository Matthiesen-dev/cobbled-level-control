package dev.matthiesen.cobbled_level_control.common.runtime;

import java.util.Map;

public record Catching(String finalStagePermission, String firstStagePermission, String middleStagePermission,
                       String singleStagePermission, String legendaryPermission, String shinyPermission,
                       Map<Integer, Integer> tierMap) {

}

package com.example.consumer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.threeten.bp.ZoneId;

public class ConsumerTest {
  @Test
  public void loadsTimeZoneRulesWithoutAndroidAssets() {
    assertNotNull(ZoneId.of("America/New_York").getRules());
  }
}

package com.example.consumer;

import android.content.Context;
import com.jakewharton.threetenabp.AndroidThreeTen;

public final class Consumer {
  public static void initialize(Context context) {
    AndroidThreeTen.init(context);
  }

  private Consumer() {}
}

package com.example.app;

import com.example.direct.Direct;
import com.example.transitive.Transitive;

public final class App {
  private final Direct direct = new Direct();
  private final Transitive transitive = new Transitive();
}

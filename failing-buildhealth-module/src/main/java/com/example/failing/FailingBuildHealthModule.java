package com.example.failing;

import com.example.direct.Direct;
import com.example.transitive.Transitive;

public final class FailingBuildHealthModule {
  private final Direct direct = new Direct();
  private final Transitive transitive = new Transitive();
}

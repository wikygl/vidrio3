package org.chromium.support_lib_boundary;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface TracingControllerBoundaryInterface {
    boolean isTracing();

    void start(int i4, Collection<String> collection, int i5);

    boolean stop(OutputStream outputStream, Executor executor);
}

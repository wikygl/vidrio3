package org.chromium.support_lib_boundary;

import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface ServiceWorkerWebSettingsBoundaryInterface {
    boolean getAllowContentAccess();

    boolean getAllowFileAccess();

    boolean getBlockNetworkLoads();

    int getCacheMode();

    Set<String> getRequestedWithHeaderOriginAllowList();

    void setAllowContentAccess(boolean z4);

    void setAllowFileAccess(boolean z4);

    void setBlockNetworkLoads(boolean z4);

    void setCacheMode(int i4);

    void setRequestedWithHeaderOriginAllowList(Set<String> set);
}

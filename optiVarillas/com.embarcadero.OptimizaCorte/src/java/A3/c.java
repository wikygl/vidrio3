package A3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends e {
    public static List m(b bVar) {
        ArrayList arrayList = new ArrayList();
        for (Object obj : bVar) {
            arrayList.add(obj);
        }
        int size = arrayList.size();
        if (size != 0) {
            if (size == 1) {
                List singletonList = Collections.singletonList(arrayList.get(0));
                h.d(singletonList, "singletonList(element)");
                return singletonList;
            }
            return arrayList;
        }
        return m3.e.f5347j;
    }
}

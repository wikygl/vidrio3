package m3;

import C1.C0149c;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class i extends C0149c {
    public static int g(int i4) {
        if (i4 >= 0) {
            if (i4 < 3) {
                return i4 + 1;
            }
            if (i4 < 1073741824) {
                return (int) ((i4 / 0.75f) + 1.0f);
            }
            return Integer.MAX_VALUE;
        }
        return i4;
    }

    public static Map h(ArrayList arrayList) {
        f fVar = f.f5348j;
        int size = arrayList.size();
        if (size != 0) {
            if (size != 1) {
                LinkedHashMap linkedHashMap = new LinkedHashMap(g(arrayList.size()));
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    l3.b bVar = (l3.b) it.next();
                    linkedHashMap.put(bVar.f5264j, bVar.f5265k);
                }
                return linkedHashMap;
            }
            l3.b bVar2 = (l3.b) arrayList.get(0);
            v3.h.e(bVar2, "pair");
            Map singletonMap = Collections.singletonMap(bVar2.f5264j, bVar2.f5265k);
            v3.h.d(singletonMap, "singletonMap(pair.first, pair.second)");
            return singletonMap;
        }
        return fVar;
    }
}

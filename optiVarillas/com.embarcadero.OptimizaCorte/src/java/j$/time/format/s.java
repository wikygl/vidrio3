package j$.time.format;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class s {

    /* renamed from: a  reason: collision with root package name */
    private final Map f3961a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public s(Map map) {
        Comparator comparator;
        Comparator comparator2;
        this.f3961a = map;
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        for (Map.Entry entry : map.entrySet()) {
            HashMap hashMap2 = new HashMap();
            for (Map.Entry entry2 : ((Map) entry.getValue()).entrySet()) {
                int i4 = c.f3925c;
                hashMap2.put((String) entry2.getValue(), new AbstractMap.SimpleImmutableEntry((String) entry2.getValue(), (Long) entry2.getKey()));
            }
            ArrayList arrayList2 = new ArrayList(hashMap2.values());
            comparator2 = c.f3924b;
            Collections.sort(arrayList2, comparator2);
            hashMap.put((w) entry.getKey(), arrayList2);
            arrayList.addAll(arrayList2);
            hashMap.put(null, arrayList);
        }
        comparator = c.f3924b;
        Collections.sort(arrayList, comparator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String a(long j4, w wVar) {
        Map map = (Map) this.f3961a.get(wVar);
        if (map != null) {
            return (String) map.get(Long.valueOf(j4));
        }
        return null;
    }
}

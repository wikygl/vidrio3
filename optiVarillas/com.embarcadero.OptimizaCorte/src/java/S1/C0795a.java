package s1;

import android.util.SparseArray;
import e0.C0405a;
import f1.d;
import java.util.HashMap;

/* renamed from: s1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0795a {

    /* renamed from: a  reason: collision with root package name */
    public static final SparseArray<d> f5761a = new SparseArray<>();

    /* renamed from: b  reason: collision with root package name */
    public static final HashMap<d, Integer> f5762b;

    static {
        HashMap<d, Integer> hashMap = new HashMap<>();
        f5762b = hashMap;
        hashMap.put(d.f3393j, 0);
        hashMap.put(d.f3394k, 1);
        hashMap.put(d.f3395l, 2);
        for (d dVar : hashMap.keySet()) {
            f5761a.append(f5762b.get(dVar).intValue(), dVar);
        }
    }

    public static int a(d dVar) {
        Integer num = f5762b.get(dVar);
        if (num != null) {
            return num.intValue();
        }
        throw new IllegalStateException("PriorityMapping is missing known Priority value " + dVar);
    }

    public static d b(int i4) {
        d dVar = f5761a.get(i4);
        if (dVar != null) {
            return dVar;
        }
        throw new IllegalArgumentException(C0405a.c("Unknown Priority for value ", i4));
    }
}

package A1;

import java.util.Comparator;
import java.util.List;
import t1.C0810l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class A1 implements Comparator {

    /* renamed from: j  reason: collision with root package name */
    public static final /* synthetic */ A1 f7j = new Object();

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        List list = C0810l.f5802c;
        return list.indexOf((String) obj) - list.indexOf((String) obj2);
    }
}

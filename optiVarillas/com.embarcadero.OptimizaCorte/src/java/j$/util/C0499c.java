package j$.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

/* renamed from: j$.util.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class C0499c implements Comparator, Serializable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4121a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Comparator f4122b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Object f4123c;

    public /* synthetic */ C0499c(Comparator comparator, Object obj, int i4) {
        this.f4121a = i4;
        this.f4122b = comparator;
        this.f4123c = obj;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f4121a) {
            case 0:
                int compare = this.f4122b.compare(obj, obj2);
                return compare != 0 ? compare : ((Comparator) this.f4123c).compare(obj, obj2);
            default:
                Function function = (Function) this.f4123c;
                return this.f4122b.compare(function.apply(obj), function.apply(obj2));
        }
    }
}

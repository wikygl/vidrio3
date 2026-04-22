package v3;

import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a implements z3.a, Serializable {

    /* renamed from: j  reason: collision with root package name */
    public transient z3.a f6299j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f6300k;

    /* renamed from: l  reason: collision with root package name */
    public final Class f6301l;

    /* renamed from: m  reason: collision with root package name */
    public final String f6302m;

    /* renamed from: n  reason: collision with root package name */
    public final String f6303n;

    /* renamed from: o  reason: collision with root package name */
    public final boolean f6304o;

    /* renamed from: v3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class C0077a implements Serializable {

        /* renamed from: j  reason: collision with root package name */
        public static final C0077a f6305j = new Object();
    }

    public a(Object obj, Class cls, String str, String str2, boolean z4) {
        this.f6300k = obj;
        this.f6301l = cls;
        this.f6302m = str;
        this.f6303n = str2;
        this.f6304o = z4;
    }

    public abstract z3.a a();

    public final b d() {
        b cVar;
        Class cls = this.f6301l;
        if (cls == null) {
            return null;
        }
        if (this.f6304o) {
            n.f6315a.getClass();
            cVar = new j(cls);
        } else {
            n.f6315a.getClass();
            cVar = new c(cls);
        }
        return cVar;
    }
}

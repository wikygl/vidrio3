package d3;

import b3.InterfaceC0359d;
import b3.InterfaceC0361f;
import b3.InterfaceC0362g;
import c3.InterfaceC0375a;
import j$.util.DesugarTimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/* renamed from: d3.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0390d implements InterfaceC0375a<C0390d> {

    /* renamed from: e  reason: collision with root package name */
    public static final C0387a f3161e = new Object();
    public static final C0388b f = new Object();

    /* renamed from: g  reason: collision with root package name */
    public static final C0389c f3162g = new Object();

    /* renamed from: h  reason: collision with root package name */
    public static final a f3163h = new Object();

    /* renamed from: a  reason: collision with root package name */
    public final HashMap f3164a;

    /* renamed from: b  reason: collision with root package name */
    public final HashMap f3165b;

    /* renamed from: c  reason: collision with root package name */
    public final C0387a f3166c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f3167d;

    /* renamed from: d3.d$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a implements InterfaceC0361f<Date> {

        /* renamed from: a  reason: collision with root package name */
        public static final SimpleDateFormat f3168a;

        static {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            f3168a = simpleDateFormat;
            simpleDateFormat.setTimeZone(DesugarTimeZone.getTimeZone("UTC"));
        }

        @Override // b3.InterfaceC0356a
        public final void a(Object obj, InterfaceC0362g interfaceC0362g) {
            interfaceC0362g.b(f3168a.format((Date) obj));
        }
    }

    public C0390d() {
        HashMap hashMap = new HashMap();
        this.f3164a = hashMap;
        HashMap hashMap2 = new HashMap();
        this.f3165b = hashMap2;
        this.f3166c = f3161e;
        this.f3167d = false;
        hashMap2.put(String.class, f);
        hashMap.remove(String.class);
        hashMap2.put(Boolean.class, f3162g);
        hashMap.remove(Boolean.class);
        hashMap2.put(Date.class, f3163h);
        hashMap.remove(Date.class);
    }

    public final InterfaceC0375a a(Class cls, InterfaceC0359d interfaceC0359d) {
        this.f3164a.put(cls, interfaceC0359d);
        this.f3165b.remove(cls);
        return this;
    }
}

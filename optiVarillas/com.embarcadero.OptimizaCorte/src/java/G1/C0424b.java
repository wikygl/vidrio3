package g1;

import B0.v;
import S0.C0284u0;
import android.content.Context;
import android.net.ConnectivityManager;
import d3.C0390d;
import h1.AbstractC0434a;
import h1.C0435b;
import h1.C0436c;
import h1.C0437d;
import h1.e;
import h1.f;
import h1.g;
import h1.h;
import h1.i;
import h1.j;
import h1.l;
import h1.n;
import h1.o;
import h1.p;
import h1.q;
import h1.r;
import h1.t;
import j1.k;
import java.net.MalformedURLException;
import java.net.URL;
import r1.InterfaceC0782a;

/* renamed from: g1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0424b implements k {

    /* renamed from: a  reason: collision with root package name */
    public final v f3484a;

    /* renamed from: b  reason: collision with root package name */
    public final ConnectivityManager f3485b;

    /* renamed from: c  reason: collision with root package name */
    public final Context f3486c;

    /* renamed from: d  reason: collision with root package name */
    public final URL f3487d;

    /* renamed from: e  reason: collision with root package name */
    public final InterfaceC0782a f3488e;
    public final InterfaceC0782a f;

    /* renamed from: g  reason: collision with root package name */
    public final int f3489g;

    /* renamed from: g1.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final URL f3490a;

        /* renamed from: b  reason: collision with root package name */
        public final o f3491b;

        /* renamed from: c  reason: collision with root package name */
        public final String f3492c;

        public a(URL url, i iVar, String str) {
            this.f3490a = url;
            this.f3491b = iVar;
            this.f3492c = str;
        }
    }

    /* renamed from: g1.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class C0051b {

        /* renamed from: a  reason: collision with root package name */
        public final int f3493a;

        /* renamed from: b  reason: collision with root package name */
        public final URL f3494b;

        /* renamed from: c  reason: collision with root package name */
        public final long f3495c;

        public C0051b(int i4, URL url, long j4) {
            this.f3493a = i4;
            this.f3494b = url;
            this.f3495c = j4;
        }
    }

    public C0424b(Context context, InterfaceC0782a interfaceC0782a, InterfaceC0782a interfaceC0782a2) {
        C0390d c0390d = new C0390d();
        C0436c c0436c = C0436c.f3519a;
        c0390d.a(o.class, c0436c);
        c0390d.a(i.class, c0436c);
        f fVar = f.f3531a;
        c0390d.a(r.class, fVar);
        c0390d.a(l.class, fVar);
        C0437d c0437d = C0437d.f3521a;
        c0390d.a(p.class, c0437d);
        c0390d.a(j.class, c0437d);
        C0435b c0435b = C0435b.f3507a;
        c0390d.a(AbstractC0434a.class, c0435b);
        c0390d.a(h.class, c0435b);
        e eVar = e.f3524a;
        c0390d.a(q.class, eVar);
        c0390d.a(h1.k.class, eVar);
        g gVar = g.f3538a;
        c0390d.a(t.class, gVar);
        c0390d.a(n.class, gVar);
        c0390d.f3167d = true;
        this.f3484a = new v(c0390d);
        this.f3486c = context;
        this.f3485b = (ConnectivityManager) context.getSystemService("connectivity");
        this.f3487d = c(C0423a.f3479c);
        this.f3488e = interfaceC0782a2;
        this.f = interfaceC0782a;
        this.f3489g = 130000;
    }

    public static URL c(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e4) {
            throw new IllegalArgumentException(C0284u0.c("Invalid url: ", str), e4);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:190:0x044f A[Catch: IOException -> 0x0487, TryCatch #12 {IOException -> 0x0487, blocks: (B:92:0x031b, B:94:0x032e, B:96:0x033f, B:105:0x0363, B:188:0x044b, B:190:0x044f, B:193:0x0464, B:197:0x0474, B:199:0x047a, B:211:0x0496, B:213:0x04a0, B:215:0x04aa, B:107:0x0370, B:120:0x03a7, B:138:0x03c5, B:182:0x0422, B:187:0x043c, B:108:0x0374, B:110:0x037e, B:115:0x039e, B:113:0x0386), top: B:227:0x031b }] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x0461  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0464 A[Catch: IOException -> 0x0487, TryCatch #12 {IOException -> 0x0487, blocks: (B:92:0x031b, B:94:0x032e, B:96:0x033f, B:105:0x0363, B:188:0x044b, B:190:0x044f, B:193:0x0464, B:197:0x0474, B:199:0x047a, B:211:0x0496, B:213:0x04a0, B:215:0x04aa, B:107:0x0370, B:120:0x03a7, B:138:0x03c5, B:182:0x0422, B:187:0x043c, B:108:0x0374, B:110:0x037e, B:115:0x039e, B:113:0x0386), top: B:227:0x031b }] */
    /* JADX WARN: Removed duplicated region for block: B:199:0x047a A[Catch: IOException -> 0x0487, TryCatch #12 {IOException -> 0x0487, blocks: (B:92:0x031b, B:94:0x032e, B:96:0x033f, B:105:0x0363, B:188:0x044b, B:190:0x044f, B:193:0x0464, B:197:0x0474, B:199:0x047a, B:211:0x0496, B:213:0x04a0, B:215:0x04aa, B:107:0x0370, B:120:0x03a7, B:138:0x03c5, B:182:0x0422, B:187:0x043c, B:108:0x0374, B:110:0x037e, B:115:0x039e, B:113:0x0386), top: B:227:0x031b }] */
    /* JADX WARN: Removed duplicated region for block: B:204:0x0489  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x0474 A[ADDED_TO_REGION, EDGE_INSN: B:269:0x0474->B:197:0x0474 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r3v36, types: [h1.k$a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v15, types: [h1.k$a, java.lang.Object] */
    @Override // j1.k
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final j1.C0664b a(j1.C0663a r38) {
        /*
            Method dump skipped, instructions count: 1221
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: g1.C0424b.a(j1.a):j1.b");
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x00a1, code lost:
        if (h1.t.a.f3578j.get(r0) != null) goto L14;
     */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0102  */
    @Override // j1.k
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final i1.h b(i1.h r7) {
        /*
            Method dump skipped, instructions count: 282
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: g1.C0424b.b(i1.h):i1.h");
    }
}

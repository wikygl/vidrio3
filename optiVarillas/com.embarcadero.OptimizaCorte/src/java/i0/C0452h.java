package i0;

import android.adservices.topics.GetTopicsRequest;
import android.adservices.topics.TopicsManager;
import android.annotation.SuppressLint;

@SuppressLint({"NewApi"})
/* renamed from: i0.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0452h extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final TopicsManager f3599k;

    @p3.e(c = "androidx.privacysandbox.ads.adservices.topics.TopicsManagerImplCommon", f = "TopicsManagerImplCommon.kt", l = {22}, m = "getTopics$suspendImpl")
    /* renamed from: i0.h$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a extends p3.c {

        /* renamed from: m  reason: collision with root package name */
        public C0452h f3600m;

        /* renamed from: n  reason: collision with root package name */
        public /* synthetic */ Object f3601n;

        /* renamed from: p  reason: collision with root package name */
        public int f3603p;

        public a(n3.d<? super a> dVar) {
            super(dVar);
        }

        @Override // p3.a
        public final Object i(Object obj) {
            this.f3601n = obj;
            this.f3603p |= Integer.MIN_VALUE;
            return C0452h.G(C0452h.this, null, this);
        }
    }

    public C0452h(TopicsManager topicsManager) {
        v3.h.e(topicsManager, "mTopicsManager");
        this.f3599k = topicsManager;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0021  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0031  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x007d A[LOOP:0: B:18:0x0077->B:20:0x007d, LOOP_END] */
    /* JADX WARN: Type inference failed for: r2v1, types: [h0.i, java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.Object G(i0.C0452h r7, i0.C0445a r8, n3.d<? super i0.C0446b> r9) {
        /*
            boolean r0 = r9 instanceof i0.C0452h.a
            if (r0 == 0) goto L13
            r0 = r9
            i0.h$a r0 = (i0.C0452h.a) r0
            int r1 = r0.f3603p
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.f3603p = r1
            goto L18
        L13:
            i0.h$a r0 = new i0.h$a
            r0.<init>(r9)
        L18:
            java.lang.Object r9 = r0.f3601n
            o3.a r1 = o3.a.f5500j
            int r2 = r0.f3603p
            r3 = 1
            if (r2 == 0) goto L31
            if (r2 != r3) goto L29
            i0.h r7 = r0.f3600m
            B2.a.n(r9)
            goto L5e
        L29:
            java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
            java.lang.String r8 = "call to 'resume' before 'invoke' with coroutine"
            r7.<init>(r8)
            throw r7
        L31:
            B2.a.n(r9)
            android.adservices.topics.GetTopicsRequest r8 = r7.F(r8)
            r0.f3600m = r7
            r0.f3603p = r3
            C3.e r9 = new C3.e
            n3.d r0 = C3.C.d(r0)
            r9.<init>(r0)
            r9.m()
            android.adservices.topics.TopicsManager r0 = r7.f3599k
            h0.i r2 = new h0.i
            r2.<init>()
            I.e r3 = new I.e
            r3.<init>(r9)
            h0.h.c(r0, r8, r2, r3)
            java.lang.Object r9 = r9.l()
            if (r9 != r1) goto L5e
            return r1
        L5e:
            android.adservices.topics.GetTopicsResponse r8 = i0.C0451g.a(r9)
            r7.getClass()
            java.lang.String r7 = "response"
            v3.h.e(r8, r7)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.List r8 = h0.C0432c.b(r8)
            java.util.Iterator r8 = r8.iterator()
        L77:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L9b
            java.lang.Object r9 = r8.next()
            android.adservices.topics.Topic r9 = h0.C0433d.a(r9)
            i0.c r6 = new i0.c
            long r2 = h0.e.a(r9)
            long r4 = h0.f.a(r9)
            int r1 = h0.g.a(r9)
            r0 = r6
            r0.<init>(r1, r2, r4)
            r7.add(r6)
            goto L77
        L9b:
            i0.b r8 = new i0.b
            r8.<init>(r7)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: i0.C0452h.G(i0.h, i0.a, n3.d):java.lang.Object");
    }

    public GetTopicsRequest F(C0445a c0445a) {
        GetTopicsRequest.Builder adsSdkName;
        GetTopicsRequest build;
        v3.h.e(c0445a, "request");
        adsSdkName = N2.a.a().setAdsSdkName(c0445a.f3593a);
        build = adsSdkName.build();
        v3.h.d(build, "Builder()\n            .s…ame)\n            .build()");
        return build;
    }

    @Override // G3.g
    public Object t(C0445a c0445a, n3.d<? super C0446b> dVar) {
        return G(this, c0445a, dVar);
    }
}

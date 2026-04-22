package j2;

import K1.C0210d;
import a2.C0345c;
import android.util.Log;
import java.util.concurrent.ExecutorService;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class G {

    /* renamed from: i  reason: collision with root package name */
    public static volatile G f4783i;

    /* renamed from: a  reason: collision with root package name */
    public final String f4784a;

    /* renamed from: b  reason: collision with root package name */
    public final C0345c f4785b;

    /* renamed from: c  reason: collision with root package name */
    public final ExecutorService f4786c;

    /* renamed from: d  reason: collision with root package name */
    public final C0210d f4787d;

    /* renamed from: e  reason: collision with root package name */
    public int f4788e;
    public boolean f;

    /* renamed from: g  reason: collision with root package name */
    public final String f4789g;

    /* renamed from: h  reason: collision with root package name */
    public volatile InterfaceC0675e f4790h;

    /* JADX WARN: Removed duplicated region for block: B:33:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x006e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0080 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public G(android.content.Context r11, java.lang.String r12, android.os.Bundle r13) {
        /*
            r10 = this;
            r10.<init>()
            java.lang.String r0 = "com.google.firebase.analytics.FirebaseAnalytics"
            if (r12 == 0) goto L10
            java.lang.Class.forName(r0)     // Catch: java.lang.ClassNotFoundException -> Lb
            goto L10
        Lb:
            java.lang.String r1 = "FA-Ads"
            r10.f4784a = r1
            goto L14
        L10:
            java.lang.String r1 = "FA"
            r10.f4784a = r1
        L14:
            a2.c r1 = a2.C0345c.f2861a
            r10.f4785b = r1
            j2.y r9 = new j2.y
            r9.<init>()
            java.util.concurrent.ThreadPoolExecutor r1 = new java.util.concurrent.ThreadPoolExecutor
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.SECONDS
            java.util.concurrent.LinkedBlockingQueue r8 = new java.util.concurrent.LinkedBlockingQueue
            r8.<init>()
            r5 = 60
            r3 = 1
            r4 = 1
            r2 = r1
            r2.<init>(r3, r4, r5, r7, r8, r9)
            r2 = 1
            r1.allowCoreThreadTimeOut(r2)
            java.util.concurrent.ExecutorService r1 = java.util.concurrent.Executors.unconfigurableExecutorService(r1)
            r10.f4786c = r1
            K1.d r1 = new K1.d
            r1.<init>(r10)
            r10.f4787d = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.String r1 = m2.C0737a.a(r11)     // Catch: java.lang.IllegalStateException -> L68
            android.content.res.Resources r3 = r11.getResources()     // Catch: java.lang.IllegalStateException -> L68
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch: java.lang.IllegalStateException -> L68
            if (r4 != 0) goto L53
            goto L57
        L53:
            java.lang.String r1 = m2.C0737a.a(r11)     // Catch: java.lang.IllegalStateException -> L68
        L57:
            java.lang.String r4 = "string"
            java.lang.String r5 = "google_app_id"
            int r1 = r3.getIdentifier(r5, r4, r1)     // Catch: java.lang.IllegalStateException -> L68
            r4 = 0
            if (r1 != 0) goto L63
            goto L6b
        L63:
            java.lang.String r1 = r3.getString(r1)     // Catch: java.lang.IllegalStateException -> L68 android.content.res.Resources.NotFoundException -> L6a
            goto L6c
        L68:
            goto L7e
        L6a:
        L6b:
            r1 = r4
        L6c:
            if (r1 == 0) goto L7e
            java.lang.Class.forName(r0)     // Catch: java.lang.ClassNotFoundException -> L72
            goto L7e
        L72:
            r10.f4789g = r4
            r10.f = r2
            java.lang.String r11 = r10.f4784a
            java.lang.String r12 = "Disabling data collection. Found google_app_id in strings.xml but Google Analytics for Firebase is missing. Remove this value or add Google Analytics for Firebase to resume data collection."
            android.util.Log.w(r11, r12)
            return
        L7e:
            if (r12 == 0) goto L89
            java.lang.Class.forName(r0)     // Catch: java.lang.ClassNotFoundException -> L84
            goto L89
        L84:
            java.lang.String r0 = "am"
            r10.f4789g = r0
            goto La4
        L89:
            java.lang.String r0 = "fa"
            r10.f4789g = r0
            if (r12 == 0) goto L97
            java.lang.String r0 = r10.f4784a
            java.lang.String r1 = "Deferring to Google Analytics for Firebase for event data collection. https://goo.gl/J1sWQy"
            android.util.Log.v(r0, r1)
            goto La4
        L97:
            if (r12 != 0) goto L9a
            goto L9b
        L9a:
            r2 = 0
        L9b:
            if (r2 == 0) goto La4
            java.lang.String r0 = r10.f4784a
            java.lang.String r1 = "Specified origin or custom app id is null. Both parameters will be ignored."
            android.util.Log.w(r0, r1)
        La4:
            j2.o r0 = new j2.o
            r0.<init>(r10, r12, r11, r13)
            r10.b(r0)
            android.content.Context r11 = r11.getApplicationContext()
            android.app.Application r11 = (android.app.Application) r11
            if (r11 != 0) goto Lbc
            java.lang.String r11 = r10.f4784a
            java.lang.String r12 = "Unable to register lifecycle notifications. Application null."
            android.util.Log.w(r11, r12)
            return
        Lbc:
            j2.F r12 = new j2.F
            r12.<init>(r10)
            r11.registerActivityLifecycleCallbacks(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j2.G.<init>(android.content.Context, java.lang.String, android.os.Bundle):void");
    }

    public final void a(Exception exc, boolean z4, boolean z5) {
        this.f |= z4;
        String str = this.f4784a;
        if (z4) {
            Log.w(str, "Data collection startup failed. No data will be collected.", exc);
            return;
        }
        if (z5) {
            b(new w(this, exc));
        }
        Log.w(str, "Error with data collection. Data lost.", exc);
    }

    public final void b(C c4) {
        this.f4786c.execute(c4);
    }
}

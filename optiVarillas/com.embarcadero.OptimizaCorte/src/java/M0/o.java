package M0;

import N0.a;
import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.xl;
import java.util.HashMap;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class o implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1700j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f1701k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1702l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f1703m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Object f1704n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Object f1705o;

    public /* synthetic */ o(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, int i4) {
        this.f1700j = i4;
        this.f1705o = obj;
        this.f1701k = obj2;
        this.f1702l = obj3;
        this.f1703m = obj4;
        this.f1704n = obj5;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // java.lang.Runnable
    public final void run() {
        char c4;
        String str;
        switch (this.f1700j) {
            case 0:
                try {
                    if (!(((N0.c) this.f1701k).f1764j instanceof a.b)) {
                        String uuid = ((UUID) this.f1702l).toString();
                        C0.o f = ((L0.r) ((p) this.f1705o).f1708c).f(uuid);
                        if (f != null && !f.a()) {
                            ((D0.c) ((p) this.f1705o).f1707b).g(uuid, (C0.f) this.f1703m);
                            ((Context) this.f1704n).startService(androidx.work.impl.foreground.a.b((Context) this.f1704n, uuid, (C0.f) this.f1703m));
                        } else {
                            throw new IllegalStateException("Calls to setForegroundAsync() must complete before a ListenableWorker signals completion of work by returning an instance of Result.");
                        }
                    }
                    ((N0.c) this.f1701k).j(null);
                    return;
                } catch (Throwable th) {
                    ((N0.c) this.f1701k).k(th);
                    return;
                }
            default:
                HashMap hashMap = new HashMap();
                hashMap.put("event", "precacheCanceled");
                hashMap.put("src", (String) this.f1701k);
                String str2 = (String) this.f1702l;
                if (!TextUtils.isEmpty(str2)) {
                    hashMap.put("cachedSrc", str2);
                }
                String str3 = (String) this.f1703m;
                switch (str3.hashCode()) {
                    case -1947652542:
                        if (str3.equals("interrupted")) {
                            c4 = 3;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -1396664534:
                        if (str3.equals("badUrl")) {
                            c4 = '\b';
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -1347010958:
                        if (str3.equals("inProgress")) {
                            c4 = 2;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -918817863:
                        if (str3.equals("downloadTimeout")) {
                            c4 = '\t';
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -659376217:
                        if (str3.equals("contentLengthMissing")) {
                            c4 = 0;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -642208130:
                        if (str3.equals("playerFailed")) {
                            c4 = 5;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -354048396:
                        if (str3.equals("sizeExceeded")) {
                            c4 = 11;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case -32082395:
                        if (str3.equals("externalAbort")) {
                            c4 = '\n';
                            break;
                        }
                        c4 = 65535;
                        break;
                    case 3387234:
                        if (str3.equals("noop")) {
                            c4 = 4;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case 96784904:
                        if (str3.equals("error")) {
                            c4 = 1;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case 580119100:
                        if (str3.equals("expireFailed")) {
                            c4 = 6;
                            break;
                        }
                        c4 = 65535;
                        break;
                    case 725497484:
                        if (str3.equals("noCacheDir")) {
                            c4 = 7;
                            break;
                        }
                        c4 = 65535;
                        break;
                    default:
                        c4 = 65535;
                        break;
                }
                switch (c4) {
                    case 6:
                    case 7:
                        str = "io";
                        break;
                    case '\b':
                    case '\t':
                        str = "network";
                        break;
                    case '\n':
                    case 11:
                        str = "policy";
                        break;
                    default:
                        str = "internal";
                        break;
                }
                hashMap.put("type", str);
                hashMap.put("reason", str3);
                String str4 = (String) this.f1704n;
                if (!TextUtils.isEmpty(str4)) {
                    hashMap.put("message", str4);
                }
                xl.i((xl) this.f1705o, hashMap);
                return;
        }
    }
}

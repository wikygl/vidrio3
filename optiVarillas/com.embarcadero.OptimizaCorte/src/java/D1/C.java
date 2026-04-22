package D1;

import com.google.android.gms.internal.ads.C5;
import com.google.android.gms.internal.ads.F5;
import com.google.android.gms.internal.ads.K5;
import com.google.android.gms.internal.ads.Y5;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C extends F5 {

    /* renamed from: v  reason: collision with root package name */
    public final Object f628v;

    /* renamed from: w  reason: collision with root package name */
    public final D f629w;

    /* renamed from: x  reason: collision with root package name */
    public final /* synthetic */ byte[] f630x;

    /* renamed from: y  reason: collision with root package name */
    public final /* synthetic */ Map f631y;

    /* renamed from: z  reason: collision with root package name */
    public final /* synthetic */ E1.l f632z;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C(int i4, String str, D d4, B b4, byte[] bArr, HashMap hashMap, E1.l lVar) {
        super(i4, str, b4);
        this.f630x = bArr;
        this.f631y = hashMap;
        this.f632z = lVar;
        this.f628v = new Object();
        this.f629w = d4;
    }

    public final K5 a(C5 c5) {
        String str;
        String str2;
        byte[] bArr = c5.b;
        try {
            Map map = c5.c;
            String str3 = "ISO-8859-1";
            if (map != null && (str2 = (String) map.get("Content-Type")) != null) {
                String[] split = str2.split(";", 0);
                int i4 = 1;
                while (true) {
                    if (i4 >= split.length) {
                        break;
                    }
                    String[] split2 = split[i4].trim().split("=", 0);
                    if (split2.length == 2 && split2[0].equals("charset")) {
                        str3 = split2[1];
                        break;
                    }
                    i4++;
                }
            }
            str = new String(bArr, str3);
        } catch (UnsupportedEncodingException unused) {
            str = new String(bArr);
        }
        return new K5(str, Y5.b(c5));
    }

    public final Map d() {
        Map map = this.f631y;
        if (map == null) {
            return Collections.emptyMap();
        }
        return map;
    }

    public final void f(Object obj) {
        D d4;
        String str = (String) obj;
        E1.l lVar = this.f632z;
        lVar.getClass();
        if (E1.l.c() && str != null) {
            lVar.d("onNetworkResponseBody", new E1.h(0, str.getBytes()));
        }
        synchronized (this.f628v) {
            d4 = this.f629w;
        }
        d4.b(str);
    }

    public final byte[] p() {
        byte[] bArr = this.f630x;
        if (bArr == null) {
            return null;
        }
        return bArr;
    }
}

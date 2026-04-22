package K0;

import L0.p;
import L0.r;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.appcompat.app.AlertController;
import androidx.work.impl.WorkDatabase;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.bw;
import j$.util.concurrent.ConcurrentHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1266j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f1267k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1268l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f1269m;

    public /* synthetic */ b(Zv zv, String str, Pair[] pairArr) {
        this.f1266j = 1;
        this.f1268l = zv;
        this.f1267k = str;
        this.f1269m = pairArr;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1266j) {
            case 0:
                p i4 = ((r) ((WorkDatabase) this.f1268l).n()).i((String) this.f1267k);
                if (i4 != null && i4.b()) {
                    synchronized (((androidx.work.impl.foreground.a) this.f1269m).l) {
                        ((androidx.work.impl.foreground.a) this.f1269m).o.put((String) this.f1267k, i4);
                        ((androidx.work.impl.foreground.a) this.f1269m).p.add(i4);
                        androidx.work.impl.foreground.a aVar = (androidx.work.impl.foreground.a) this.f1269m;
                        aVar.q.c(aVar.p);
                    }
                    return;
                }
                return;
            case 1:
                Zv zv = (Zv) this.f1268l;
                zv.getClass();
                ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(((bw) zv).a);
                if (!TextUtils.isEmpty("action")) {
                    String str = (String) this.f1267k;
                    if (!TextUtils.isEmpty(str)) {
                        concurrentHashMap.put("action", str);
                    }
                }
                int i5 = 0;
                while (true) {
                    Pair[] pairArr = (Pair[]) this.f1269m;
                    if (i5 < pairArr.length) {
                        Pair pair = pairArr[i5];
                        String str2 = (String) pair.first;
                        String str3 = (String) pair.second;
                        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                            concurrentHashMap.put(str2, str3);
                        }
                        i5++;
                    } else {
                        zv.a(concurrentHashMap, false);
                        return;
                    }
                }
                break;
            default:
                AlertController.b(((AlertController) this.f1269m).w, (View) this.f1268l, (View) this.f1267k);
                return;
        }
    }

    public /* synthetic */ b(Object obj, Object obj2, Object obj3, int i4) {
        this.f1266j = i4;
        this.f1269m = obj;
        this.f1268l = obj2;
        this.f1267k = obj3;
    }
}

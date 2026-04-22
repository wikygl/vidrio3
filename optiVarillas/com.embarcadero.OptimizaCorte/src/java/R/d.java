package R;

import M.C0226h;
import M.O;
import R.g;
import a1.InterfaceC0342a;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class d implements InterfaceC0342a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ KeyEvent.Callback f2030j;

    public /* synthetic */ d(KeyEvent.Callback callback) {
        this.f2030j = callback;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [M.h$c, java.lang.Object] */
    public boolean a(g gVar, int i4, Bundle bundle) {
        C0226h.a aVar;
        int i5 = Build.VERSION.SDK_INT;
        if (i5 >= 25 && (i4 & 1) != 0) {
            try {
                gVar.f2033a.c();
                Parcelable parcelable = (Parcelable) gVar.f2033a.a();
                if (bundle == null) {
                    bundle = new Bundle();
                } else {
                    bundle = new Bundle(bundle);
                }
                bundle.putParcelable("androidx.core.view.extra.INPUT_CONTENT_INFO", parcelable);
            } catch (Exception e4) {
                Log.w("InputConnectionCompat", "Can't insert content from IME; requestPermission() failed", e4);
                return false;
            }
        }
        ClipDescription description = gVar.f2033a.getDescription();
        g.c cVar = gVar.f2033a;
        ClipData clipData = new ClipData(description, new ClipData.Item(cVar.b()));
        if (i5 >= 31) {
            aVar = new C0226h.a(clipData, 2);
        } else {
            ?? obj = new Object();
            obj.f1618a = clipData;
            obj.f1619b = 2;
            aVar = obj;
        }
        aVar.c(cVar.d());
        aVar.b(bundle);
        if (O.l((View) this.f2030j, aVar.a()) != null) {
            return false;
        }
        return true;
    }

    @Override // a1.InterfaceC0342a
    public void b(int i4, b1.e eVar) {
        ActivityOptimizacion activityOptimizacion = (ActivityOptimizacion) this.f2030j;
        activityOptimizacion.f3074T.set(i4, eVar);
        C0353a c0353a = activityOptimizacion.f3076V;
        ArrayList<b1.e> arrayList = activityOptimizacion.f3074T;
        boolean z4 = c0353a.f2897x;
        c0353a.f2875a = ActivityOptimizacion.H(arrayList);
    }
}

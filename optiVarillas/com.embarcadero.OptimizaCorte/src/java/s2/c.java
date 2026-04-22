package s2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.internal.ads.bI;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class c<V extends View> extends CoordinatorLayout.c<V> {

    /* renamed from: a  reason: collision with root package name */
    public bI f5775a;

    /* renamed from: b  reason: collision with root package name */
    public int f5776b;

    public c() {
        this.f5776b = 0;
    }

    /* JADX WARN: Type inference failed for: r1v8, types: [com.google.android.gms.internal.ads.bI, java.lang.Object] */
    public boolean h(CoordinatorLayout coordinatorLayout, V v4, int i4) {
        u(coordinatorLayout, v4, i4);
        if (this.f5775a == null) {
            ?? obj = new Object();
            ((bI) obj).d = v4;
            this.f5775a = obj;
        }
        bI bIVar = this.f5775a;
        View view = (View) bIVar.d;
        bIVar.a = view.getTop();
        bIVar.b = view.getLeft();
        this.f5775a.a();
        int i5 = this.f5776b;
        if (i5 != 0) {
            bI bIVar2 = this.f5775a;
            if (bIVar2.c != i5) {
                bIVar2.c = i5;
                bIVar2.a();
            }
            this.f5776b = 0;
            return true;
        }
        return true;
    }

    public final int s() {
        bI bIVar = this.f5775a;
        if (bIVar != null) {
            return bIVar.c;
        }
        return 0;
    }

    public int t() {
        return s();
    }

    public void u(CoordinatorLayout coordinatorLayout, V v4, int i4) {
        coordinatorLayout.q(v4, i4);
    }

    public c(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f5776b = 0;
    }
}

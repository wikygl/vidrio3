package D;

import D.f;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class g implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f538j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f539k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f540l;

    public /* synthetic */ g(Object obj, int i4, Object obj2) {
        this.f538j = i4;
        this.f539k = obj;
        this.f540l = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Object obj = this.f540l;
        Object obj2 = this.f539k;
        switch (this.f538j) {
            case 0:
                ((f.e) obj2).c((Typeface) obj);
                return;
            default:
                int i4 = ActivityInicio.f2947A0;
                ((InputMethodManager) ((ActivityInicio) obj2).getSystemService("input_method")).showSoftInput((EditText) obj, 1);
                return;
        }
    }
}

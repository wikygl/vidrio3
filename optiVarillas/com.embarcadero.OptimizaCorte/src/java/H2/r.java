package H2;

import C.a;
import M.O;
import M.V;
import M.k0;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class r implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ View f1120j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ boolean f1121k = false;

    @Override // java.lang.Runnable
    public final void run() {
        String str;
        Object obj;
        k0 k0Var;
        View view = this.f1120j;
        Object obj2 = null;
        if (this.f1121k) {
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            if (Build.VERSION.SDK_INT >= 30) {
                k0Var = O.j.c(view);
            } else {
                Context context = view.getContext();
                while (true) {
                    if (!(context instanceof ContextWrapper)) {
                        break;
                    } else if (context instanceof Activity) {
                        Window window = ((Activity) context).getWindow();
                        if (window != null) {
                            k0Var = new k0(window, view);
                        }
                    } else {
                        context = ((ContextWrapper) context).getBaseContext();
                    }
                }
                k0Var = null;
            }
            if (k0Var != null) {
                k0Var.f1638a.c();
                return;
            }
        }
        Context context2 = view.getContext();
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 23) {
            obj = a.b.b(context2, InputMethodManager.class);
        } else {
            if (i4 >= 23) {
                str = a.b.c(context2, InputMethodManager.class);
            } else {
                str = a.d.f297a.get(InputMethodManager.class);
            }
            if (str != null) {
                obj2 = context2.getSystemService(str);
            }
            obj = obj2;
        }
        ((InputMethodManager) obj).showSoftInput(view, 1);
    }
}

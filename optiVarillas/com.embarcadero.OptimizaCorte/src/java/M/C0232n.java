package M;

import M.O;
import android.os.Build;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* renamed from: M.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0232n {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f1645a;

    /* renamed from: b  reason: collision with root package name */
    public static Method f1646b;

    /* renamed from: c  reason: collision with root package name */
    public static boolean f1647c;

    /* renamed from: d  reason: collision with root package name */
    public static Field f1648d;

    /* renamed from: M.n$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface a {
        boolean d(KeyEvent keyEvent);
    }

    public static boolean a(View view, KeyEvent keyEvent) {
        WeakReference<View> weakReference;
        int indexOfKey;
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        ArrayList<WeakReference<View>> arrayList = O.n.f1541d;
        O.n nVar = (O.n) view.getTag(2131231254);
        if (nVar == null) {
            nVar = new O.n();
            view.setTag(2131231254, nVar);
        }
        WeakReference<KeyEvent> weakReference2 = nVar.f1544c;
        if (weakReference2 != null && weakReference2.get() == keyEvent) {
            return false;
        }
        nVar.f1544c = new WeakReference<>(keyEvent);
        if (nVar.f1543b == null) {
            nVar.f1543b = new SparseArray<>();
        }
        SparseArray<WeakReference<View>> sparseArray = nVar.f1543b;
        if (keyEvent.getAction() == 1 && (indexOfKey = sparseArray.indexOfKey(keyEvent.getKeyCode())) >= 0) {
            weakReference = sparseArray.valueAt(indexOfKey);
            sparseArray.removeAt(indexOfKey);
        } else {
            weakReference = null;
        }
        if (weakReference == null) {
            weakReference = sparseArray.get(keyEvent.getKeyCode());
        }
        if (weakReference == null) {
            return false;
        }
        View view2 = weakReference.get();
        if (view2 != null && view2.isAttachedToWindow()) {
            O.n.b(view2, keyEvent);
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x00c1  */
    /* JADX WARN: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"LambdaLast"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean b(M.C0232n.a r7, android.view.View r8, android.view.Window.Callback r9, android.view.KeyEvent r10) {
        /*
            r0 = 1
            r1 = 0
            if (r7 != 0) goto L5
            return r1
        L5:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r2 < r3) goto L10
            boolean r7 = r7.d(r10)
            return r7
        L10:
            boolean r2 = r9 instanceof android.app.Activity
            r3 = 0
            if (r2 == 0) goto L83
            android.app.Activity r9 = (android.app.Activity) r9
            r9.onUserInteraction()
            android.view.Window r7 = r9.getWindow()
            r8 = 8
            boolean r8 = r7.hasFeature(r8)
            if (r8 == 0) goto L66
            android.app.ActionBar r8 = r9.getActionBar()
            int r2 = r10.getKeyCode()
            r4 = 82
            if (r2 != r4) goto L66
            if (r8 == 0) goto L66
            boolean r2 = M.C0232n.f1645a
            if (r2 != 0) goto L4c
            java.lang.Class r2 = r8.getClass()     // Catch: java.lang.NoSuchMethodException -> L4a
            java.lang.String r4 = "onMenuKeyEvent"
            java.lang.Class[] r5 = new java.lang.Class[r0]     // Catch: java.lang.NoSuchMethodException -> L4a
            java.lang.Class<android.view.KeyEvent> r6 = android.view.KeyEvent.class
            r5[r1] = r6     // Catch: java.lang.NoSuchMethodException -> L4a
            java.lang.reflect.Method r2 = r2.getMethod(r4, r5)     // Catch: java.lang.NoSuchMethodException -> L4a
            M.C0232n.f1646b = r2     // Catch: java.lang.NoSuchMethodException -> L4a
        L4a:
            M.C0232n.f1645a = r0
        L4c:
            java.lang.reflect.Method r2 = M.C0232n.f1646b
            if (r2 == 0) goto L63
            java.lang.Object[] r4 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L62
            r4[r1] = r10     // Catch: java.lang.Throwable -> L62
            java.lang.Object r8 = r2.invoke(r8, r4)     // Catch: java.lang.Throwable -> L62
            if (r8 != 0) goto L5b
            goto L63
        L5b:
            java.lang.Boolean r8 = (java.lang.Boolean) r8     // Catch: java.lang.Throwable -> L62
            boolean r1 = r8.booleanValue()     // Catch: java.lang.Throwable -> L62
            goto L63
        L62:
        L63:
            if (r1 == 0) goto L66
            goto L82
        L66:
            boolean r8 = r7.superDispatchKeyEvent(r10)
            if (r8 == 0) goto L6d
            goto L82
        L6d:
            android.view.View r7 = r7.getDecorView()
            boolean r8 = M.O.b(r7, r10)
            if (r8 == 0) goto L78
            goto L82
        L78:
            if (r7 == 0) goto L7e
            android.view.KeyEvent$DispatcherState r3 = r7.getKeyDispatcherState()
        L7e:
            boolean r0 = r10.dispatch(r9, r3, r9)
        L82:
            return r0
        L83:
            boolean r2 = r9 instanceof android.app.Dialog
            if (r2 == 0) goto Ld7
            android.app.Dialog r9 = (android.app.Dialog) r9
            boolean r7 = M.C0232n.f1647c
            if (r7 != 0) goto L9c
            java.lang.Class<android.app.Dialog> r7 = android.app.Dialog.class
            java.lang.String r8 = "mOnKeyListener"
            java.lang.reflect.Field r7 = r7.getDeclaredField(r8)     // Catch: java.lang.NoSuchFieldException -> L9a
            M.C0232n.f1648d = r7     // Catch: java.lang.NoSuchFieldException -> L9a
            r7.setAccessible(r0)     // Catch: java.lang.NoSuchFieldException -> L9a
        L9a:
            M.C0232n.f1647c = r0
        L9c:
            java.lang.reflect.Field r7 = M.C0232n.f1648d
            if (r7 == 0) goto La8
            java.lang.Object r7 = r7.get(r9)     // Catch: java.lang.IllegalAccessException -> La7
            android.content.DialogInterface$OnKeyListener r7 = (android.content.DialogInterface.OnKeyListener) r7     // Catch: java.lang.IllegalAccessException -> La7
            goto La9
        La7:
        La8:
            r7 = r3
        La9:
            if (r7 == 0) goto Lb6
            int r8 = r10.getKeyCode()
            boolean r7 = r7.onKey(r9, r8, r10)
            if (r7 == 0) goto Lb6
            goto Ld6
        Lb6:
            android.view.Window r7 = r9.getWindow()
            boolean r8 = r7.superDispatchKeyEvent(r10)
            if (r8 == 0) goto Lc1
            goto Ld6
        Lc1:
            android.view.View r7 = r7.getDecorView()
            boolean r8 = M.O.b(r7, r10)
            if (r8 == 0) goto Lcc
            goto Ld6
        Lcc:
            if (r7 == 0) goto Ld2
            android.view.KeyEvent$DispatcherState r3 = r7.getKeyDispatcherState()
        Ld2:
            boolean r0 = r10.dispatch(r9, r3, r9)
        Ld6:
            return r0
        Ld7:
            if (r8 == 0) goto Ldf
            boolean r8 = M.O.b(r8, r10)
            if (r8 != 0) goto Le7
        Ldf:
            boolean r7 = r7.d(r10)
            if (r7 == 0) goto Le6
            goto Le7
        Le6:
            r0 = 0
        Le7:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: M.C0232n.b(M.n$a, android.view.View, android.view.Window$Callback, android.view.KeyEvent):boolean");
    }
}

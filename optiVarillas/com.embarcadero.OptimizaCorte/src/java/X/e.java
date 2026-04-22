package X;

import android.text.Editable;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;
import android.view.View;
import androidx.emoji2.text.k;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class e implements KeyListener {

    /* renamed from: a  reason: collision with root package name */
    public final KeyListener f2792a;

    /* renamed from: b  reason: collision with root package name */
    public final a f2793b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [X.e$a, java.lang.Object] */
    public e(KeyListener keyListener) {
        ?? obj = new Object();
        this.f2792a = keyListener;
        this.f2793b = obj;
    }

    @Override // android.text.method.KeyListener
    public final void clearMetaKeyState(View view, Editable editable, int i4) {
        this.f2792a.clearMetaKeyState(view, editable, i4);
    }

    @Override // android.text.method.KeyListener
    public final int getInputType() {
        return this.f2792a.getInputType();
    }

    @Override // android.text.method.KeyListener
    public final boolean onKeyDown(View view, Editable editable, int i4, KeyEvent keyEvent) {
        boolean a4;
        boolean z4;
        this.f2793b.getClass();
        if (i4 != 67) {
            if (i4 != 112) {
                a4 = false;
            } else {
                a4 = k.a(editable, keyEvent, true);
            }
        } else {
            a4 = k.a(editable, keyEvent, false);
        }
        if (a4) {
            MetaKeyKeyListener.adjustMetaAfterKeypress(editable);
            z4 = true;
        } else {
            z4 = false;
        }
        if (!z4 && !this.f2792a.onKeyDown(view, editable, i4, keyEvent)) {
            return false;
        }
        return true;
    }

    @Override // android.text.method.KeyListener
    public final boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
        return this.f2792a.onKeyOther(view, editable, keyEvent);
    }

    @Override // android.text.method.KeyListener
    public final boolean onKeyUp(View view, Editable editable, int i4, KeyEvent keyEvent) {
        return this.f2792a.onKeyUp(view, editable, i4, keyEvent);
    }
}

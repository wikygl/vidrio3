package U2;

import S0.View$OnClickListenerC0251d0;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class u extends n {

    /* renamed from: e  reason: collision with root package name */
    public final int f2472e;
    public EditText f;

    /* renamed from: g  reason: collision with root package name */
    public final View$OnClickListenerC0251d0 f2473g;

    public u(com.google.android.material.textfield.a aVar, int i4) {
        super(aVar);
        this.f2472e = 2131165345;
        this.f2473g = new View$OnClickListenerC0251d0(2, this);
        if (i4 != 0) {
            this.f2472e = i4;
        }
    }

    @Override // U2.n
    public final void b() {
        q();
    }

    @Override // U2.n
    public final int c() {
        return 2131820872;
    }

    @Override // U2.n
    public final int d() {
        return this.f2472e;
    }

    @Override // U2.n
    public final View.OnClickListener f() {
        return this.f2473g;
    }

    @Override // U2.n
    public final boolean k() {
        return true;
    }

    @Override // U2.n
    public final boolean l() {
        boolean z4;
        EditText editText = this.f;
        if (editText != null && (editText.getTransformationMethod() instanceof PasswordTransformationMethod)) {
            z4 = true;
        } else {
            z4 = false;
        }
        return !z4;
    }

    @Override // U2.n
    public final void m(EditText editText) {
        this.f = editText;
        q();
    }

    @Override // U2.n
    public final void r() {
        EditText editText = this.f;
        if (editText != null) {
            if (editText.getInputType() == 16 || editText.getInputType() == 128 || editText.getInputType() == 144 || editText.getInputType() == 224) {
                this.f.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    @Override // U2.n
    public final void s() {
        EditText editText = this.f;
        if (editText != null) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}

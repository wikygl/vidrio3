package F3;

import c1.C0373f;
import com.google.android.material.textfield.TextInputEditText;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class e implements f.e {
    public static int b(int i4, int i5, int i6, int i7) {
        return ((i4 * i5) / i6) + i7;
    }

    public static void c(String str, double d4, TextInputEditText textInputEditText) {
        textInputEditText.setText(C0373f.j(d4, Long.parseLong(str)));
    }

    @Override // u0.f.e
    public void a(f.d dVar, u0.f fVar) {
        dVar.e(fVar);
    }
}

package X;

import android.text.Editable;
import androidx.emoji2.text.o;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b extends Editable.Factory {

    /* renamed from: a  reason: collision with root package name */
    public static final Object f2783a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static volatile b f2784b;

    /* renamed from: c  reason: collision with root package name */
    public static Class<?> f2785c;

    @Override // android.text.Editable.Factory
    public final Editable newEditable(CharSequence charSequence) {
        Class<?> cls = f2785c;
        if (cls != null) {
            return new o(cls, charSequence);
        }
        return super.newEditable(charSequence);
    }
}

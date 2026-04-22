package X;

import C1.C0149c;
import android.text.Editable;
import android.widget.EditText;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public final C0033a f2780a;

    /* renamed from: X.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class C0033a extends b {

        /* renamed from: a  reason: collision with root package name */
        public final EditText f2781a;

        /* renamed from: b  reason: collision with root package name */
        public final g f2782b;

        /* JADX WARN: Type inference failed for: r1v1, types: [android.text.Editable$Factory, X.b] */
        public C0033a(EditText editText) {
            this.f2781a = editText;
            g gVar = new g(editText);
            this.f2782b = gVar;
            editText.addTextChangedListener(gVar);
            if (X.b.f2784b == null) {
                synchronized (X.b.f2783a) {
                    try {
                        if (X.b.f2784b == null) {
                            ?? factory = new Editable.Factory();
                            try {
                                X.b.f2785c = Class.forName("android.text.DynamicLayout$ChangeWatcher", false, X.b.class.getClassLoader());
                            } catch (Throwable unused) {
                            }
                            X.b.f2784b = factory;
                        }
                    } finally {
                    }
                }
            }
            editText.setEditableFactory(X.b.f2784b);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b {
    }

    public a(EditText editText) {
        C0149c.e(editText, "editText cannot be null");
        this.f2780a = new C0033a(editText);
    }
}

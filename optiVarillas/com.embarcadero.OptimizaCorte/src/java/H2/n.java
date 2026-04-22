package H2;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import java.lang.ref.WeakReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class n {

    /* renamed from: c  reason: collision with root package name */
    public float f1112c;

    /* renamed from: d  reason: collision with root package name */
    public float f1113d;
    public final WeakReference<b> f;

    /* renamed from: g  reason: collision with root package name */
    public L2.d f1115g;

    /* renamed from: a  reason: collision with root package name */
    public final TextPaint f1110a = new TextPaint(1);

    /* renamed from: b  reason: collision with root package name */
    public final a f1111b = new a();

    /* renamed from: e  reason: collision with root package name */
    public boolean f1114e = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a extends G3.g {
        public a() {
        }

        @Override // G3.g
        public final void B(int i4) {
            n nVar = n.this;
            nVar.f1114e = true;
            b bVar = nVar.f.get();
            if (bVar != null) {
                bVar.a();
            }
        }

        @Override // G3.g
        public final void C(Typeface typeface, boolean z4) {
            if (z4) {
                return;
            }
            n nVar = n.this;
            nVar.f1114e = true;
            b bVar = nVar.f.get();
            if (bVar != null) {
                bVar.a();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface b {
        void a();

        int[] getState();

        boolean onStateChange(int[] iArr);
    }

    public n(b bVar) {
        this.f = new WeakReference<>(null);
        this.f = new WeakReference<>(bVar);
    }

    public final void a(String str) {
        float measureText;
        TextPaint textPaint = this.f1110a;
        float f = 0.0f;
        if (str == null) {
            measureText = 0.0f;
        } else {
            measureText = textPaint.measureText((CharSequence) str, 0, str.length());
        }
        this.f1112c = measureText;
        if (str != null) {
            f = Math.abs(textPaint.getFontMetrics().ascent);
        }
        this.f1113d = f;
        this.f1114e = false;
    }

    public final void b(L2.d dVar, Context context) {
        if (this.f1115g != dVar) {
            this.f1115g = dVar;
            if (dVar != null) {
                TextPaint textPaint = this.f1110a;
                a aVar = this.f1111b;
                dVar.f(context, textPaint, aVar);
                b bVar = this.f.get();
                if (bVar != null) {
                    textPaint.drawableState = bVar.getState();
                }
                dVar.e(context, textPaint, aVar);
                this.f1114e = true;
            }
            b bVar2 = this.f.get();
            if (bVar2 != null) {
                bVar2.a();
                bVar2.onStateChange(bVar2.getState());
            }
        }
    }
}

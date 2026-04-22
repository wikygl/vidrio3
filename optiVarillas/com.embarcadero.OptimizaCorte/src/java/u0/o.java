package u0;

import android.graphics.Rect;
import android.os.Build;
import android.util.Property;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class o {

    /* renamed from: a  reason: collision with root package name */
    public static final q f6003a;

    /* renamed from: b  reason: collision with root package name */
    public static final a f6004b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a extends Property<View, Float> {
        @Override // android.util.Property
        public final Float get(View view) {
            return Float.valueOf(o.f6003a.a(view));
        }

        @Override // android.util.Property
        public final void set(View view, Float f) {
            float floatValue = f.floatValue();
            o.f6003a.c(view, floatValue);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class b extends Property<View, Rect> {
        @Override // android.util.Property
        public final Rect get(View view) {
            return view.getClipBounds();
        }

        @Override // android.util.Property
        public final void set(View view, Rect rect) {
            view.setClipBounds(rect);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, u0.q] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, u0.q] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Object, u0.q] */
    /* JADX WARN: Type inference failed for: r0v4, types: [u0.o$a, android.util.Property] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.Object, u0.q] */
    static {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 29) {
            f6003a = new Object();
        } else if (i4 >= 23) {
            f6003a = new Object();
        } else if (i4 >= 22) {
            f6003a = new Object();
        } else {
            f6003a = new Object();
        }
        f6004b = new Property(Float.class, "translationAlpha");
        new Property(Rect.class, "clipBounds");
    }

    public static void a(View view, int i4, int i5, int i6, int i7) {
        f6003a.b(view, i4, i5, i6, i7);
    }

    public static void b(View view, int i4) {
        f6003a.d(view, i4);
    }
}

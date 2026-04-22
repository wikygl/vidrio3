package B2;

import C3.C0169t;
import C3.J;
import C3.o0;
import F3.x;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import androidx.cardview.widget.CardView;
import com.google.gson.internal.i;
import java.util.ArrayList;
import java.util.TreeSet;
import l.S;
import l3.c;
import n3.d;
import n3.f;
import q.InterfaceC0765a;
import q.b;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class a implements i {
    public static final c.a a(Throwable th) {
        h.e(th, "exception");
        return new c.a(th);
    }

    public static b b(InterfaceC0765a interfaceC0765a) {
        return (b) ((CardView.a) interfaceC0765a).a;
    }

    public static int c(Context context, int i4, int i5) {
        Integer num;
        int i6;
        TypedValue a4 = L2.b.a(context, i4);
        if (a4 != null) {
            int i7 = a4.resourceId;
            if (i7 != 0) {
                i6 = C.a.b(context, i7);
            } else {
                i6 = a4.data;
            }
            num = Integer.valueOf(i6);
        } else {
            num = null;
        }
        if (num != null) {
            return num.intValue();
        }
        return i5;
    }

    public static int d(View view, int i4) {
        Context context = view.getContext();
        TypedValue c4 = L2.b.c(i4, view.getContext(), view.getClass().getCanonicalName());
        int i5 = c4.resourceId;
        if (i5 != 0) {
            return C.a.b(context, i5);
        }
        return c4.data;
    }

    public static int e(Cursor cursor, String str) {
        int columnIndex = cursor.getColumnIndex(str);
        if (columnIndex >= 0) {
            return columnIndex;
        }
        return cursor.getColumnIndexOrThrow("`" + str + "`");
    }

    public static Drawable f(Context context, int i4) {
        return S.d().f(context, i4);
    }

    public static final boolean g(int i4) {
        if (i4 == 1 || i4 == 2) {
            return true;
        }
        return false;
    }

    public static boolean h(int i4) {
        double pow;
        double pow2;
        double pow3;
        if (i4 != 0) {
            ThreadLocal<double[]> threadLocal = E.a.f801a;
            double[] dArr = threadLocal.get();
            if (dArr == null) {
                dArr = new double[3];
                threadLocal.set(dArr);
            }
            int red = Color.red(i4);
            int green = Color.green(i4);
            int blue = Color.blue(i4);
            if (dArr.length == 3) {
                double d4 = red / 255.0d;
                if (d4 < 0.04045d) {
                    pow = d4 / 12.92d;
                } else {
                    pow = Math.pow((d4 + 0.055d) / 1.055d, 2.4d);
                }
                double d5 = green / 255.0d;
                if (d5 < 0.04045d) {
                    pow2 = d5 / 12.92d;
                } else {
                    pow2 = Math.pow((d5 + 0.055d) / 1.055d, 2.4d);
                }
                double d6 = blue / 255.0d;
                if (d6 < 0.04045d) {
                    pow3 = d6 / 12.92d;
                } else {
                    pow3 = Math.pow((d6 + 0.055d) / 1.055d, 2.4d);
                }
                dArr[0] = ((0.1805d * pow3) + (0.3576d * pow2) + (0.4124d * pow)) * 100.0d;
                double d7 = ((0.0722d * pow3) + (0.7152d * pow2) + (0.2126d * pow)) * 100.0d;
                dArr[1] = d7;
                double d8 = pow3 * 0.9505d;
                dArr[2] = (d8 + (pow2 * 0.1192d) + (pow * 0.0193d)) * 100.0d;
                if (d7 / 100.0d > 0.5d) {
                    return true;
                }
            } else {
                throw new IllegalArgumentException("outXyz must have a length of 3.");
            }
        }
        return false;
    }

    public static int i(float f, int i4, int i5) {
        return E.a.b(E.a.d(i5, Math.round(Color.alpha(i5) * f)), i4);
    }

    public static void j(AnimatorSet animatorSet, ArrayList arrayList) {
        int size = arrayList.size();
        long j4 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            Animator animator = (Animator) arrayList.get(i4);
            j4 = Math.max(j4, animator.getDuration() + animator.getStartDelay());
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(0, 0);
        ofInt.setDuration(j4);
        arrayList.add(0, ofInt);
        animatorSet.playTogether(arrayList);
    }

    public static final void l(J j4, d dVar, boolean z4) {
        Object d4;
        o0 o0Var;
        Object f = j4.f();
        Throwable c4 = j4.c(f);
        if (c4 != null) {
            d4 = a(c4);
        } else {
            d4 = j4.d(f);
        }
        if (z4) {
            h.c(dVar, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<T of kotlinx.coroutines.DispatchedTaskKt.resume>");
            F3.h hVar = (F3.h) dVar;
            d<T> dVar2 = hVar.f917n;
            f context = dVar2.getContext();
            Object b4 = x.b(context, hVar.f919p);
            if (b4 != x.f947a) {
                o0Var = C0169t.a(dVar2, context);
            } else {
                o0Var = null;
            }
            try {
                dVar2.j(d4);
                if (o0Var == null) {
                    x.a(context, b4);
                    return;
                } else {
                    o0Var.T();
                    throw null;
                }
            } catch (Throwable th) {
                if (o0Var != null) {
                    o0Var.T();
                    throw null;
                } else {
                    x.a(context, b4);
                    throw th;
                }
            }
        }
        dVar.j(d4);
    }

    public static final void n(Object obj) {
        if (!(obj instanceof c.a)) {
            return;
        }
        throw ((c.a) obj).f5266j;
    }

    public Object k() {
        return new TreeSet();
    }

    public void m(InterfaceC0765a interfaceC0765a, float f) {
        b b4 = b(interfaceC0765a);
        CardView.a aVar = (CardView.a) interfaceC0765a;
        boolean useCompatPadding = aVar.b.getUseCompatPadding();
        CardView cardView = aVar.b;
        boolean preventCornerOverlap = cardView.getPreventCornerOverlap();
        if (f != b4.f5591e || b4.f != useCompatPadding || b4.f5592g != preventCornerOverlap) {
            b4.f5591e = f;
            b4.f = useCompatPadding;
            b4.f5592g = preventCornerOverlap;
            b4.b(null);
            b4.invalidateSelf();
        }
        if (!cardView.getUseCompatPadding()) {
            aVar.a(0, 0, 0, 0);
            return;
        }
        float f4 = b(interfaceC0765a).f5591e;
        float f5 = b(interfaceC0765a).f5587a;
        int ceil = (int) Math.ceil(q.c.a(f4, f5, cardView.getPreventCornerOverlap()));
        int ceil2 = (int) Math.ceil(q.c.b(f4, f5, cardView.getPreventCornerOverlap()));
        aVar.a(ceil, ceil2, ceil, ceil2);
    }
}

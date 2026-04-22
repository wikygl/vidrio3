package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.Log;
import l.S;

/* renamed from: l.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0701j {

    /* renamed from: b  reason: collision with root package name */
    public static final PorterDuff.Mode f5161b = PorterDuff.Mode.SRC_IN;

    /* renamed from: c  reason: collision with root package name */
    public static C0701j f5162c;

    /* renamed from: a  reason: collision with root package name */
    public S f5163a;

    /* renamed from: l.j$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements S.f {

        /* renamed from: a  reason: collision with root package name */
        public final int[] f5164a = {2131165300, 2131165298, 2131165224};

        /* renamed from: b  reason: collision with root package name */
        public final int[] f5165b = {2131165248, 2131165283, 2131165255, 2131165250, 2131165251, 2131165254, 2131165253};

        /* renamed from: c  reason: collision with root package name */
        public final int[] f5166c = {2131165297, 2131165299, 2131165241, 2131165293, 2131165294, 2131165295, 2131165296};

        /* renamed from: d  reason: collision with root package name */
        public final int[] f5167d = {2131165273, 2131165239, 2131165272};

        /* renamed from: e  reason: collision with root package name */
        public final int[] f5168e = {2131165291, 2131165301};
        public final int[] f = {2131165227, 2131165233, 2131165228, 2131165234};

        public static boolean a(int[] iArr, int i4) {
            for (int i5 : iArr) {
                if (i5 == i4) {
                    return true;
                }
            }
            return false;
        }

        public static ColorStateList b(Context context, int i4) {
            int c4 = W.c(context, 2130903270);
            return new ColorStateList(new int[][]{W.f5081b, W.f5083d, W.f5082c, W.f}, new int[]{W.b(context, 2130903267), E.a.b(c4, i4), E.a.b(c4, i4), i4});
        }

        public static LayerDrawable c(S s4, Context context, int i4) {
            BitmapDrawable bitmapDrawable;
            BitmapDrawable bitmapDrawable2;
            BitmapDrawable bitmapDrawable3;
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(i4);
            Drawable f = s4.f(context, 2131165287);
            Drawable f4 = s4.f(context, 2131165288);
            if ((f instanceof BitmapDrawable) && f.getIntrinsicWidth() == dimensionPixelSize && f.getIntrinsicHeight() == dimensionPixelSize) {
                bitmapDrawable = (BitmapDrawable) f;
                bitmapDrawable2 = new BitmapDrawable(bitmapDrawable.getBitmap());
            } else {
                Bitmap createBitmap = Bitmap.createBitmap(dimensionPixelSize, dimensionPixelSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                f.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
                f.draw(canvas);
                bitmapDrawable = new BitmapDrawable(createBitmap);
                bitmapDrawable2 = new BitmapDrawable(createBitmap);
            }
            bitmapDrawable2.setTileModeX(Shader.TileMode.REPEAT);
            if ((f4 instanceof BitmapDrawable) && f4.getIntrinsicWidth() == dimensionPixelSize && f4.getIntrinsicHeight() == dimensionPixelSize) {
                bitmapDrawable3 = (BitmapDrawable) f4;
            } else {
                Bitmap createBitmap2 = Bitmap.createBitmap(dimensionPixelSize, dimensionPixelSize, Bitmap.Config.ARGB_8888);
                Canvas canvas2 = new Canvas(createBitmap2);
                f4.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
                f4.draw(canvas2);
                bitmapDrawable3 = new BitmapDrawable(createBitmap2);
            }
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{bitmapDrawable, bitmapDrawable3, bitmapDrawable2});
            layerDrawable.setId(0, 16908288);
            layerDrawable.setId(1, 16908303);
            layerDrawable.setId(2, 16908301);
            return layerDrawable;
        }

        public static void e(Drawable drawable, int i4) {
            drawable.mutate().setColorFilter(C0701j.c(i4, C0701j.f5161b));
        }

        public final ColorStateList d(Context context, int i4) {
            if (i4 == 2131165244) {
                return C.a.c(context, 2131034134);
            }
            if (i4 == 2131165290) {
                return C.a.c(context, 2131034137);
            }
            if (i4 == 2131165289) {
                int[][] iArr = new int[3];
                int[] iArr2 = new int[3];
                ColorStateList d4 = W.d(context, 2130903321);
                if (d4 != null && d4.isStateful()) {
                    int[] iArr3 = W.f5081b;
                    iArr[0] = iArr3;
                    iArr2[0] = d4.getColorForState(iArr3, 0);
                    iArr[1] = W.f5084e;
                    iArr2[1] = W.c(context, 2130903269);
                    iArr[2] = W.f;
                    iArr2[2] = d4.getDefaultColor();
                } else {
                    iArr[0] = W.f5081b;
                    iArr2[0] = W.b(context, 2130903321);
                    iArr[1] = W.f5084e;
                    iArr2[1] = W.c(context, 2130903269);
                    iArr[2] = W.f;
                    iArr2[2] = W.c(context, 2130903321);
                }
                return new ColorStateList(iArr, iArr2);
            } else if (i4 == 2131165232) {
                return b(context, W.c(context, 2130903267));
            } else {
                if (i4 == 2131165226) {
                    return b(context, 0);
                }
                if (i4 == 2131165231) {
                    return b(context, W.c(context, 2130903265));
                }
                if (i4 != 2131165285 && i4 != 2131165286) {
                    if (a(this.f5165b, i4)) {
                        return W.d(context, 2130903271);
                    }
                    if (a(this.f5168e, i4)) {
                        return C.a.c(context, 2131034133);
                    }
                    if (a(this.f, i4)) {
                        return C.a.c(context, 2131034132);
                    }
                    if (i4 == 2131165282) {
                        return C.a.c(context, 2131034135);
                    }
                    return null;
                }
                return C.a.c(context, 2131034136);
            }
        }
    }

    public static synchronized C0701j a() {
        C0701j c0701j;
        synchronized (C0701j.class) {
            try {
                if (f5162c == null) {
                    d();
                }
                c0701j = f5162c;
            } catch (Throwable th) {
                throw th;
            }
        }
        return c0701j;
    }

    public static synchronized PorterDuffColorFilter c(int i4, PorterDuff.Mode mode) {
        PorterDuffColorFilter h4;
        synchronized (C0701j.class) {
            h4 = S.h(i4, mode);
        }
        return h4;
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [l.j, java.lang.Object] */
    public static synchronized void d() {
        synchronized (C0701j.class) {
            if (f5162c == null) {
                ?? obj = new Object();
                f5162c = obj;
                obj.f5163a = S.d();
                f5162c.f5163a.l(new a());
            }
        }
    }

    public static void e(Drawable drawable, Z z4, int[] iArr) {
        ColorStateList colorStateList;
        PorterDuff.Mode mode;
        PorterDuff.Mode mode2 = S.f5057h;
        int[] state = drawable.getState();
        if (drawable.mutate() == drawable) {
            if ((drawable instanceof LayerDrawable) && drawable.isStateful()) {
                drawable.setState(new int[0]);
                drawable.setState(state);
            }
            boolean z5 = z4.f5090d;
            if (!z5 && !z4.f5089c) {
                drawable.clearColorFilter();
            } else {
                PorterDuffColorFilter porterDuffColorFilter = null;
                if (z5) {
                    colorStateList = z4.f5087a;
                } else {
                    colorStateList = null;
                }
                if (z4.f5089c) {
                    mode = z4.f5088b;
                } else {
                    mode = S.f5057h;
                }
                if (colorStateList != null && mode != null) {
                    porterDuffColorFilter = S.h(colorStateList.getColorForState(iArr, 0), mode);
                }
                drawable.setColorFilter(porterDuffColorFilter);
            }
            if (Build.VERSION.SDK_INT <= 23) {
                drawable.invalidateSelf();
                return;
            }
            return;
        }
        Log.d("ResourceManagerInternal", "Mutated drawable is not the same instance as the input.");
    }

    public final synchronized Drawable b(Context context, int i4) {
        return this.f5163a.f(context, i4);
    }
}

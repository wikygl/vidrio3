package t2;

import H2.n;
import H2.p;
import L2.d;
import P2.f;
import P2.i;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import t2.C0818b;

/* renamed from: t2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0817a extends Drawable implements n.b {

    /* renamed from: j  reason: collision with root package name */
    public final WeakReference<Context> f5817j;

    /* renamed from: k  reason: collision with root package name */
    public final f f5818k;

    /* renamed from: l  reason: collision with root package name */
    public final n f5819l;

    /* renamed from: m  reason: collision with root package name */
    public final Rect f5820m;

    /* renamed from: n  reason: collision with root package name */
    public final C0818b f5821n;

    /* renamed from: o  reason: collision with root package name */
    public float f5822o;

    /* renamed from: p  reason: collision with root package name */
    public float f5823p;

    /* renamed from: q  reason: collision with root package name */
    public final int f5824q;

    /* renamed from: r  reason: collision with root package name */
    public float f5825r;

    /* renamed from: s  reason: collision with root package name */
    public float f5826s;

    /* renamed from: t  reason: collision with root package name */
    public float f5827t;

    /* renamed from: u  reason: collision with root package name */
    public WeakReference<View> f5828u;

    /* renamed from: v  reason: collision with root package name */
    public WeakReference<FrameLayout> f5829v;

    public C0817a(Context context) {
        int intValue;
        int intValue2;
        FrameLayout frameLayout;
        d dVar;
        WeakReference<Context> weakReference = new WeakReference<>(context);
        this.f5817j = weakReference;
        p.c(context, p.f1118b, "Theme.MaterialComponents");
        this.f5820m = new Rect();
        n nVar = new n(this);
        this.f5819l = nVar;
        TextPaint textPaint = nVar.f1110a;
        textPaint.setTextAlign(Paint.Align.CENTER);
        C0818b c0818b = new C0818b(context);
        this.f5821n = c0818b;
        boolean e4 = e();
        C0818b.a aVar = c0818b.f5831b;
        if (e4) {
            intValue = aVar.f5859p.intValue();
        } else {
            intValue = aVar.f5857n.intValue();
        }
        if (e()) {
            intValue2 = aVar.f5860q.intValue();
        } else {
            intValue2 = aVar.f5858o.intValue();
        }
        f fVar = new f(i.a(context, intValue, intValue2, new P2.a(0)).a());
        this.f5818k = fVar;
        g();
        Context context2 = weakReference.get();
        if (context2 != null && nVar.f1115g != (dVar = new d(context2, aVar.f5856m.intValue()))) {
            nVar.b(dVar, context2);
            textPaint.setColor(aVar.f5855l.intValue());
            invalidateSelf();
            i();
            invalidateSelf();
        }
        int i4 = aVar.f5864u;
        if (i4 != -2) {
            this.f5824q = ((int) Math.pow(10.0d, i4 - 1.0d)) - 1;
        } else {
            this.f5824q = aVar.f5865v;
        }
        nVar.f1114e = true;
        i();
        invalidateSelf();
        nVar.f1114e = true;
        g();
        i();
        invalidateSelf();
        textPaint.setAlpha(getAlpha());
        invalidateSelf();
        ColorStateList valueOf = ColorStateList.valueOf(aVar.f5854k.intValue());
        if (fVar.f1820j.f1840c != valueOf) {
            fVar.l(valueOf);
            invalidateSelf();
        }
        textPaint.setColor(aVar.f5855l.intValue());
        invalidateSelf();
        WeakReference<View> weakReference2 = this.f5828u;
        if (weakReference2 != null && weakReference2.get() != null) {
            View view = this.f5828u.get();
            WeakReference<FrameLayout> weakReference3 = this.f5829v;
            if (weakReference3 != null) {
                frameLayout = weakReference3.get();
            } else {
                frameLayout = null;
            }
            h(view, frameLayout);
        }
        i();
        setVisible(aVar.f5842C.booleanValue(), false);
    }

    @Override // H2.n.b
    public final void a() {
        invalidateSelf();
    }

    public final String b() {
        boolean z4;
        int i4 = this.f5824q;
        C0818b c0818b = this.f5821n;
        C0818b.a aVar = c0818b.f5831b;
        String str = aVar.f5862s;
        if (str != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        WeakReference<Context> weakReference = this.f5817j;
        if (z4) {
            int i5 = aVar.f5864u;
            if (i5 != -2 && str != null && str.length() > i5) {
                Context context = weakReference.get();
                if (context == null) {
                    return "";
                }
                return String.format(context.getString(2131820740), str.substring(0, i5 - 1), "…");
            }
            return str;
        } else if (f()) {
            C0818b.a aVar2 = c0818b.f5831b;
            if (i4 != -2 && d() > i4) {
                Context context2 = weakReference.get();
                if (context2 == null) {
                    return "";
                }
                return String.format(aVar2.f5866w, context2.getString(2131820806), Integer.valueOf(i4), "+");
            }
            return NumberFormat.getInstance(aVar2.f5866w).format(d());
        } else {
            return null;
        }
    }

    public final FrameLayout c() {
        WeakReference<FrameLayout> weakReference = this.f5829v;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public final int d() {
        int i4 = this.f5821n.f5831b.f5863t;
        if (i4 == -1) {
            return 0;
        }
        return i4;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        String b4;
        int round;
        if (!getBounds().isEmpty() && getAlpha() != 0 && isVisible()) {
            this.f5818k.draw(canvas);
            if (e() && (b4 = b()) != null) {
                Rect rect = new Rect();
                n nVar = this.f5819l;
                nVar.f1110a.getTextBounds(b4, 0, b4.length(), rect);
                float exactCenterY = this.f5823p - rect.exactCenterY();
                float f = this.f5822o;
                if (rect.bottom <= 0) {
                    round = (int) exactCenterY;
                } else {
                    round = Math.round(exactCenterY);
                }
                canvas.drawText(b4, f, round, nVar.f1110a);
            }
        }
    }

    public final boolean e() {
        if (this.f5821n.f5831b.f5862s != null || f()) {
            return true;
        }
        return false;
    }

    public final boolean f() {
        C0818b.a aVar = this.f5821n.f5831b;
        if (aVar.f5862s == null && aVar.f5863t != -1) {
            return true;
        }
        return false;
    }

    public final void g() {
        int intValue;
        int intValue2;
        Context context = this.f5817j.get();
        if (context == null) {
            return;
        }
        boolean e4 = e();
        C0818b c0818b = this.f5821n;
        if (e4) {
            intValue = c0818b.f5831b.f5859p.intValue();
        } else {
            intValue = c0818b.f5831b.f5857n.intValue();
        }
        if (e()) {
            intValue2 = c0818b.f5831b.f5860q.intValue();
        } else {
            intValue2 = c0818b.f5831b.f5858o.intValue();
        }
        this.f5818k.setShapeAppearanceModel(i.a(context, intValue, intValue2, new P2.a(0)).a());
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        return this.f5821n.f5831b.f5861r;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return this.f5820m.height();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return this.f5820m.width();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -3;
    }

    public final void h(View view, FrameLayout frameLayout) {
        this.f5828u = new WeakReference<>(view);
        this.f5829v = new WeakReference<>(frameLayout);
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.setClipChildren(false);
        viewGroup.setClipToPadding(false);
        i();
        invalidateSelf();
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x0220  */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0241  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x025e  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x026b  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0278  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0285  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void i() {
        /*
            Method dump skipped, instructions count: 736
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: t2.C0817a.i():void");
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        return false;
    }

    @Override // android.graphics.drawable.Drawable, H2.n.b
    public final boolean onStateChange(int[] iArr) {
        return super.onStateChange(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        C0818b c0818b = this.f5821n;
        c0818b.f5830a.f5861r = i4;
        c0818b.f5831b.f5861r = i4;
        this.f5819l.f1110a.setAlpha(getAlpha());
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
    }
}

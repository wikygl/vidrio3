package v0;

import F.a;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import r.C0773b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d extends g implements Animatable {

    /* renamed from: p  reason: collision with root package name */
    public static final /* synthetic */ int f6211p = 0;

    /* renamed from: k  reason: collision with root package name */
    public final b f6212k;

    /* renamed from: l  reason: collision with root package name */
    public final Context f6213l;

    /* renamed from: m  reason: collision with root package name */
    public R2.b f6214m;

    /* renamed from: n  reason: collision with root package name */
    public ArrayList<AbstractC0830c> f6215n;

    /* renamed from: o  reason: collision with root package name */
    public final a f6216o;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a implements Drawable.Callback {
        public a() {
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void invalidateDrawable(Drawable drawable) {
            d.this.invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j4) {
            d.this.scheduleSelf(runnable, j4);
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            d.this.unscheduleSelf(runnable);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public h f6218a;

        /* renamed from: b  reason: collision with root package name */
        public AnimatorSet f6219b;

        /* renamed from: c  reason: collision with root package name */
        public ArrayList<Animator> f6220c;

        /* renamed from: d  reason: collision with root package name */
        public C0773b<Animator, String> f6221d;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources) {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [android.graphics.drawable.Drawable$ConstantState, v0.d$b] */
    public d(Context context) {
        this.f6214m = null;
        this.f6215n = null;
        this.f6216o = new a();
        this.f6213l = context;
        this.f6212k = new Drawable.ConstantState();
    }

    @Override // v0.g, android.graphics.drawable.Drawable
    public final void applyTheme(Resources.Theme theme) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            a.C0006a.a(drawable, theme);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean canApplyTheme() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return a.C0006a.b(drawable);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.draw(canvas);
            return;
        }
        b bVar = this.f6212k;
        bVar.f6218a.draw(canvas);
        if (bVar.f6219b.isStarted()) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getAlpha();
        }
        return this.f6212k.f6218a.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getChangingConfigurations();
        }
        int changingConfigurations = super.getChangingConfigurations();
        this.f6212k.getClass();
        return changingConfigurations | 0;
    }

    @Override // android.graphics.drawable.Drawable
    public final ColorFilter getColorFilter() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return a.C0006a.c(drawable);
        }
        return this.f6212k.f6218a.getColorFilter();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        if (this.f6225j != null && Build.VERSION.SDK_INT >= 24) {
            return new c(this.f6225j.getConstantState());
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return this.f6212k.f6218a.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return this.f6212k.f6218a.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.getOpacity();
        }
        return this.f6212k.f6218a.getOpacity();
    }

    /* JADX WARN: Code restructure failed: missing block: B:86:0x018a, code lost:
        if (r8.f6219b != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x018c, code lost:
        r8.f6219b = new android.animation.AnimatorSet();
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0193, code lost:
        r8.f6219b.playTogether(r8.f6220c);
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x019a, code lost:
        return;
     */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00a7  */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void inflate(android.content.res.Resources r24, org.xmlpull.v1.XmlPullParser r25, android.util.AttributeSet r26, android.content.res.Resources.Theme r27) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: v0.d.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.isAutoMirrored();
        }
        return this.f6212k.f6218a.isAutoMirrored();
    }

    @Override // android.graphics.drawable.Animatable
    public final boolean isRunning() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return ((AnimatedVectorDrawable) drawable).isRunning();
        }
        return this.f6212k.f6219b.isRunning();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.isStateful();
        }
        return this.f6212k.f6218a.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.mutate();
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setBounds(rect);
        } else {
            this.f6212k.f6218a.setBounds(rect);
        }
    }

    @Override // v0.g, android.graphics.drawable.Drawable
    public final boolean onLevelChange(int i4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.setLevel(i4);
        }
        return this.f6212k.f6218a.setLevel(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onStateChange(int[] iArr) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.setState(iArr);
        }
        return this.f6212k.f6218a.setState(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setAlpha(i4);
        } else {
            this.f6212k.f6218a.setAlpha(i4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAutoMirrored(boolean z4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setAutoMirrored(z4);
        } else {
            this.f6212k.f6218a.setAutoMirrored(z4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        } else {
            this.f6212k.f6218a.setColorFilter(colorFilter);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            F.a.d(drawable, i4);
        } else {
            this.f6212k.f6218a.setTint(i4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            F.a.e(drawable, colorStateList);
        } else {
            this.f6212k.f6218a.setTintList(colorStateList);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            F.a.f(drawable, mode);
        } else {
            this.f6212k.f6218a.setTintMode(mode);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean z4, boolean z5) {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            return drawable.setVisible(z4, z5);
        }
        this.f6212k.f6218a.setVisible(z4, z5);
        return super.setVisible(z4, z5);
    }

    @Override // android.graphics.drawable.Animatable
    public final void start() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).start();
            return;
        }
        b bVar = this.f6212k;
        if (bVar.f6219b.isStarted()) {
            return;
        }
        bVar.f6219b.start();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Animatable
    public final void stop() {
        Drawable drawable = this.f6225j;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).stop();
        } else {
            this.f6212k.f6219b.end();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public final Drawable.ConstantState f6222a;

        public c(Drawable.ConstantState constantState) {
            this.f6222a = constantState;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final boolean canApplyTheme() {
            return this.f6222a.canApplyTheme();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.f6222a.getChangingConfigurations();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            d dVar = new d();
            Drawable newDrawable = this.f6222a.newDrawable();
            dVar.f6225j = newDrawable;
            newDrawable.setCallback(dVar.f6216o);
            return dVar;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources) {
            d dVar = new d();
            Drawable newDrawable = this.f6222a.newDrawable(resources);
            dVar.f6225j = newDrawable;
            newDrawable.setCallback(dVar.f6216o);
            return dVar;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources, Resources.Theme theme) {
            d dVar = new d();
            Drawable newDrawable = this.f6222a.newDrawable(resources, theme);
            dVar.f6225j = newDrawable;
            newDrawable.setCallback(dVar.f6216o);
            return dVar;
        }
    }

    public d() {
        this(null);
    }

    @Override // android.graphics.drawable.Drawable
    public final void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
        inflate(resources, xmlPullParser, attributeSet, null);
    }
}

package F;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f extends Drawable.ConstantState {

    /* renamed from: a  reason: collision with root package name */
    public int f894a;

    /* renamed from: b  reason: collision with root package name */
    public Drawable.ConstantState f895b;

    /* renamed from: c  reason: collision with root package name */
    public ColorStateList f896c;

    /* renamed from: d  reason: collision with root package name */
    public PorterDuff.Mode f897d;

    @Override // android.graphics.drawable.Drawable.ConstantState
    public final int getChangingConfigurations() {
        int i4;
        int i5 = this.f894a;
        Drawable.ConstantState constantState = this.f895b;
        if (constantState != null) {
            i4 = constantState.getChangingConfigurations();
        } else {
            i4 = 0;
        }
        return i5 | i4;
    }

    @Override // android.graphics.drawable.Drawable.ConstantState
    public final Drawable newDrawable() {
        return newDrawable(null);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.graphics.drawable.Drawable, F.d] */
    @Override // android.graphics.drawable.Drawable.ConstantState
    public final Drawable newDrawable(Resources resources) {
        ?? drawable = new Drawable();
        drawable.f890m = this;
        Drawable.ConstantState constantState = this.f895b;
        if (constantState != null) {
            drawable.a(constantState.newDrawable(resources));
        }
        e.f();
        return drawable;
    }
}

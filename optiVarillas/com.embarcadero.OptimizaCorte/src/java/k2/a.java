package K2;

import S.b;
import android.content.res.ColorStateList;
import l.C0709s;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a extends C0709s {

    /* renamed from: p  reason: collision with root package name */
    public static final int[][] f1419p = {new int[]{16842910, 16842912}, new int[]{16842910, -16842912}, new int[]{-16842910, 16842912}, new int[]{-16842910, -16842912}};

    /* renamed from: n  reason: collision with root package name */
    public ColorStateList f1420n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f1421o;

    private ColorStateList getMaterialThemeColorsTintList() {
        if (this.f1420n == null) {
            int d4 = B2.a.d(this, 2130903269);
            int d5 = B2.a.d(this, 2130903288);
            int d6 = B2.a.d(this, 2130903311);
            this.f1420n = new ColorStateList(f1419p, new int[]{B2.a.i(1.0f, d6, d4), B2.a.i(0.54f, d6, d5), B2.a.i(0.38f, d6, d5), B2.a.i(0.38f, d6, d5)});
        }
        return this.f1420n;
    }

    @Override // android.widget.TextView, android.view.View
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.f1421o && b.a.a(this) == null) {
            setUseMaterialThemeColors(true);
        }
    }

    public void setUseMaterialThemeColors(boolean z4) {
        this.f1421o = z4;
        if (z4) {
            b.a.c(this, getMaterialThemeColorsTintList());
        } else {
            b.a.c(this, null);
        }
    }
}

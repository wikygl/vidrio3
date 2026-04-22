package x2;

import F.a;
import M.O;
import M.V;
import M2.b;
import P2.f;
import P2.i;
import P2.m;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import java.util.WeakHashMap;

/* renamed from: x2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0858a {

    /* renamed from: u  reason: collision with root package name */
    public static final boolean f6474u;

    /* renamed from: v  reason: collision with root package name */
    public static final boolean f6475v;

    /* renamed from: a  reason: collision with root package name */
    public final MaterialButton f6476a;

    /* renamed from: b  reason: collision with root package name */
    public i f6477b;

    /* renamed from: c  reason: collision with root package name */
    public int f6478c;

    /* renamed from: d  reason: collision with root package name */
    public int f6479d;

    /* renamed from: e  reason: collision with root package name */
    public int f6480e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public int f6481g;

    /* renamed from: h  reason: collision with root package name */
    public int f6482h;

    /* renamed from: i  reason: collision with root package name */
    public PorterDuff.Mode f6483i;

    /* renamed from: j  reason: collision with root package name */
    public ColorStateList f6484j;

    /* renamed from: k  reason: collision with root package name */
    public ColorStateList f6485k;

    /* renamed from: l  reason: collision with root package name */
    public ColorStateList f6486l;

    /* renamed from: m  reason: collision with root package name */
    public Drawable f6487m;

    /* renamed from: q  reason: collision with root package name */
    public boolean f6491q;

    /* renamed from: s  reason: collision with root package name */
    public LayerDrawable f6493s;

    /* renamed from: t  reason: collision with root package name */
    public int f6494t;

    /* renamed from: n  reason: collision with root package name */
    public boolean f6488n = false;

    /* renamed from: o  reason: collision with root package name */
    public boolean f6489o = false;

    /* renamed from: p  reason: collision with root package name */
    public boolean f6490p = false;

    /* renamed from: r  reason: collision with root package name */
    public boolean f6492r = true;

    static {
        int i4 = Build.VERSION.SDK_INT;
        boolean z4 = true;
        f6474u = true;
        if (i4 > 22) {
            z4 = false;
        }
        f6475v = z4;
    }

    public C0858a(MaterialButton materialButton, i iVar) {
        this.f6476a = materialButton;
        this.f6477b = iVar;
    }

    public final m a() {
        LayerDrawable layerDrawable = this.f6493s;
        if (layerDrawable != null && layerDrawable.getNumberOfLayers() > 1) {
            if (this.f6493s.getNumberOfLayers() > 2) {
                return (m) this.f6493s.getDrawable(2);
            }
            return (m) this.f6493s.getDrawable(1);
        }
        return null;
    }

    public final f b(boolean z4) {
        LayerDrawable layerDrawable = this.f6493s;
        if (layerDrawable != null && layerDrawable.getNumberOfLayers() > 0) {
            if (f6474u) {
                return (f) ((LayerDrawable) ((InsetDrawable) this.f6493s.getDrawable(0)).getDrawable()).getDrawable(!z4 ? 1 : 0);
            }
            return (f) this.f6493s.getDrawable(!z4 ? 1 : 0);
        }
        return null;
    }

    public final void c(i iVar) {
        this.f6477b = iVar;
        if (f6475v && !this.f6489o) {
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            MaterialButton materialButton = this.f6476a;
            int paddingStart = materialButton.getPaddingStart();
            int paddingTop = materialButton.getPaddingTop();
            int paddingEnd = materialButton.getPaddingEnd();
            int paddingBottom = materialButton.getPaddingBottom();
            e();
            materialButton.setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);
            return;
        }
        if (b(false) != null) {
            b(false).setShapeAppearanceModel(iVar);
        }
        if (b(true) != null) {
            b(true).setShapeAppearanceModel(iVar);
        }
        if (a() != null) {
            a().setShapeAppearanceModel(iVar);
        }
    }

    public final void d(int i4, int i5) {
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        MaterialButton materialButton = this.f6476a;
        int paddingStart = materialButton.getPaddingStart();
        int paddingTop = materialButton.getPaddingTop();
        int paddingEnd = materialButton.getPaddingEnd();
        int paddingBottom = materialButton.getPaddingBottom();
        int i6 = this.f6480e;
        int i7 = this.f;
        this.f = i5;
        this.f6480e = i4;
        if (!this.f6489o) {
            e();
        }
        materialButton.setPaddingRelative(paddingStart, (paddingTop + i4) - i6, paddingEnd, (paddingBottom + i5) - i7);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v11, types: [android.graphics.drawable.LayerDrawable, android.graphics.drawable.RippleDrawable] */
    /* JADX WARN: Type inference failed for: r8v2, types: [android.graphics.drawable.Drawable$ConstantState, M2.a$a] */
    public final void e() {
        int i4;
        InsetDrawable insetDrawable;
        f fVar = new f(this.f6477b);
        MaterialButton materialButton = this.f6476a;
        fVar.j(materialButton.getContext());
        a.C0006a.h(fVar, this.f6484j);
        PorterDuff.Mode mode = this.f6483i;
        if (mode != null) {
            a.C0006a.i(fVar, mode);
        }
        ColorStateList colorStateList = this.f6485k;
        fVar.f1820j.f1847k = this.f6482h;
        fVar.invalidateSelf();
        f.b bVar = fVar.f1820j;
        if (bVar.f1841d != colorStateList) {
            bVar.f1841d = colorStateList;
            fVar.onStateChange(fVar.getState());
        }
        f fVar2 = new f(this.f6477b);
        fVar2.setTint(0);
        float f = this.f6482h;
        if (this.f6488n) {
            i4 = B2.a.d(materialButton, 2130903311);
        } else {
            i4 = 0;
        }
        fVar2.f1820j.f1847k = f;
        fVar2.invalidateSelf();
        ColorStateList valueOf = ColorStateList.valueOf(i4);
        f.b bVar2 = fVar2.f1820j;
        if (bVar2.f1841d != valueOf) {
            bVar2.f1841d = valueOf;
            fVar2.onStateChange(fVar2.getState());
        }
        if (f6474u) {
            f fVar3 = new f(this.f6477b);
            this.f6487m = fVar3;
            a.C0006a.g(fVar3, -1);
            ?? rippleDrawable = new RippleDrawable(b.b(this.f6486l), new InsetDrawable((Drawable) new LayerDrawable(new Drawable[]{fVar2, fVar}), this.f6478c, this.f6480e, this.f6479d, this.f), this.f6487m);
            this.f6493s = rippleDrawable;
            insetDrawable = rippleDrawable;
        } else {
            f fVar4 = new f(this.f6477b);
            ?? constantState = new Drawable.ConstantState();
            constantState.f1731a = fVar4;
            constantState.f1732b = false;
            M2.a aVar = new M2.a(constantState);
            this.f6487m = aVar;
            a.C0006a.h(aVar, b.b(this.f6486l));
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{fVar2, fVar, this.f6487m});
            this.f6493s = layerDrawable;
            insetDrawable = new InsetDrawable((Drawable) layerDrawable, this.f6478c, this.f6480e, this.f6479d, this.f);
        }
        materialButton.setInternalBackground(insetDrawable);
        f b4 = b(false);
        if (b4 != null) {
            b4.k(this.f6494t);
            b4.setState(materialButton.getDrawableState());
        }
    }

    public final void f() {
        int i4 = 0;
        f b4 = b(false);
        f b5 = b(true);
        if (b4 != null) {
            ColorStateList colorStateList = this.f6485k;
            b4.f1820j.f1847k = this.f6482h;
            b4.invalidateSelf();
            f.b bVar = b4.f1820j;
            if (bVar.f1841d != colorStateList) {
                bVar.f1841d = colorStateList;
                b4.onStateChange(b4.getState());
            }
            if (b5 != null) {
                float f = this.f6482h;
                if (this.f6488n) {
                    i4 = B2.a.d(this.f6476a, 2130903311);
                }
                b5.f1820j.f1847k = f;
                b5.invalidateSelf();
                ColorStateList valueOf = ColorStateList.valueOf(i4);
                f.b bVar2 = b5.f1820j;
                if (bVar2.f1841d != valueOf) {
                    bVar2.f1841d = valueOf;
                    b5.onStateChange(b5.getState());
                }
            }
        }
    }
}

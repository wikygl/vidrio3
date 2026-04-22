package g;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.StateSet;
import g.C0420b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class d extends C0420b {

    /* renamed from: w  reason: collision with root package name */
    public a f3472w;

    /* renamed from: x  reason: collision with root package name */
    public boolean f3473x;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends C0420b.c {

        /* renamed from: H  reason: collision with root package name */
        public int[][] f3474H;

        public a(a aVar, d dVar, Resources resources) {
            super(aVar, dVar, resources);
            if (aVar != null) {
                this.f3474H = aVar.f3474H;
            } else {
                this.f3474H = new int[this.f3451g.length];
            }
        }

        @Override // g.C0420b.c
        public void e() {
            int[] iArr;
            int[][] iArr2 = this.f3474H;
            int[][] iArr3 = new int[iArr2.length];
            for (int length = iArr2.length - 1; length >= 0; length--) {
                int[] iArr4 = this.f3474H[length];
                if (iArr4 != null) {
                    iArr = (int[]) iArr4.clone();
                } else {
                    iArr = null;
                }
                iArr3[length] = iArr;
            }
            this.f3474H = iArr3;
        }

        public final int f(int[] iArr) {
            int[][] iArr2 = this.f3474H;
            int i4 = this.f3452h;
            for (int i5 = 0; i5 < i4; i5++) {
                if (StateSet.stateSetMatches(iArr2[i5], iArr)) {
                    return i5;
                }
            }
            return -1;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new d(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new d(this, resources);
        }
    }

    public d(a aVar, Resources resources) {
        e(new a(aVar, this, resources));
        onStateChange(getState());
    }

    @Override // g.C0420b, android.graphics.drawable.Drawable
    public final void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        onStateChange(getState());
    }

    @Override // g.C0420b
    public void e(C0420b.c cVar) {
        this.f3425j = cVar;
        int i4 = this.f3431p;
        if (i4 >= 0) {
            Drawable d4 = cVar.d(i4);
            this.f3427l = d4;
            if (d4 != null) {
                c(d4);
            }
        }
        this.f3428m = null;
        if (cVar instanceof a) {
            this.f3472w = (a) cVar;
        }
    }

    @Override // g.C0420b
    /* renamed from: f */
    public a b() {
        return new a(this.f3472w, this, null);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // g.C0420b, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.f3473x) {
            super.mutate();
            this.f3472w.e();
            this.f3473x = true;
        }
        return this;
    }

    @Override // g.C0420b, android.graphics.drawable.Drawable
    public boolean onStateChange(int[] iArr) {
        boolean onStateChange = super.onStateChange(iArr);
        int f = this.f3472w.f(iArr);
        if (f < 0) {
            f = this.f3472w.f(StateSet.WILD_CARD);
        }
        if (!d(f) && !onStateChange) {
            return false;
        }
        return true;
    }
}

package l;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.ActionMenuView;
import d.C0376a;

/* renamed from: l.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0692a extends ViewGroup {

    /* renamed from: j  reason: collision with root package name */
    public final C0057a f5091j;

    /* renamed from: k  reason: collision with root package name */
    public final Context f5092k;

    /* renamed from: l  reason: collision with root package name */
    public ActionMenuView f5093l;

    /* renamed from: m  reason: collision with root package name */
    public androidx.appcompat.widget.a f5094m;

    /* renamed from: n  reason: collision with root package name */
    public int f5095n;

    /* renamed from: o  reason: collision with root package name */
    public M.V f5096o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f5097p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f5098q;

    /* renamed from: l.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0057a implements M.W {

        /* renamed from: j  reason: collision with root package name */
        public boolean f5099j = false;

        /* renamed from: k  reason: collision with root package name */
        public int f5100k;

        public C0057a() {
        }

        @Override // M.W
        public final void b() {
            if (this.f5099j) {
                return;
            }
            AbstractC0692a abstractC0692a = AbstractC0692a.this;
            abstractC0692a.f5096o = null;
            AbstractC0692a.super.setVisibility(this.f5100k);
        }

        @Override // M.W
        public final void c() {
            this.f5099j = true;
        }

        @Override // M.W
        public final void e() {
            AbstractC0692a.super.setVisibility(0);
            this.f5099j = false;
        }
    }

    public AbstractC0692a(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public static int c(View view, int i4, int i5) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE), i5);
        return Math.max(0, i4 - view.getMeasuredWidth());
    }

    public static int d(int i4, int i5, int i6, View view, boolean z4) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i7 = ((i6 - measuredHeight) / 2) + i5;
        if (z4) {
            view.layout(i4 - measuredWidth, i7, i4, measuredHeight + i7);
        } else {
            view.layout(i4, i7, i4 + measuredWidth, measuredHeight + i7);
        }
        if (z4) {
            return -measuredWidth;
        }
        return measuredWidth;
    }

    public final M.V e(int i4, long j4) {
        M.V v4 = this.f5096o;
        if (v4 != null) {
            v4.b();
        }
        C0057a c0057a = this.f5091j;
        if (i4 == 0) {
            if (getVisibility() != 0) {
                setAlpha(0.0f);
            }
            M.V a4 = M.O.a(this);
            a4.a(1.0f);
            a4.c(j4);
            AbstractC0692a.this.f5096o = a4;
            c0057a.f5100k = i4;
            a4.d(c0057a);
            return a4;
        }
        M.V a5 = M.O.a(this);
        a5.a(0.0f);
        a5.c(j4);
        AbstractC0692a.this.f5096o = a5;
        c0057a.f5100k = i4;
        a5.d(c0057a);
        return a5;
    }

    public int getAnimatedVisibility() {
        if (this.f5096o != null) {
            return this.f5091j.f5100k;
        }
        return getVisibility();
    }

    public int getContentHeight() {
        return this.f5095n;
    }

    @Override // android.view.View
    public final void onConfigurationChanged(Configuration configuration) {
        int i4;
        super.onConfigurationChanged(configuration);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, C0376a.f3129a, 2130903045, 0);
        setContentHeight(obtainStyledAttributes.getLayoutDimension(13, 0));
        obtainStyledAttributes.recycle();
        androidx.appcompat.widget.a aVar = this.f5094m;
        if (aVar != null) {
            Configuration configuration2 = ((androidx.appcompat.view.menu.a) aVar).k.getResources().getConfiguration();
            int i5 = configuration2.screenWidthDp;
            int i6 = configuration2.screenHeightDp;
            if (configuration2.smallestScreenWidthDp <= 600 && i5 <= 600 && ((i5 <= 960 || i6 <= 720) && (i5 <= 720 || i6 <= 960))) {
                if (i5 < 500 && ((i5 <= 640 || i6 <= 480) && (i5 <= 480 || i6 <= 640))) {
                    if (i5 >= 360) {
                        i4 = 3;
                    } else {
                        i4 = 2;
                    }
                } else {
                    i4 = 4;
                }
            } else {
                i4 = 5;
            }
            aVar.y = i4;
            androidx.appcompat.view.menu.f fVar = ((androidx.appcompat.view.menu.a) aVar).l;
            if (fVar != null) {
                fVar.p(true);
            }
        }
    }

    @Override // android.view.View
    public final boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 9) {
            this.f5098q = false;
        }
        if (!this.f5098q) {
            boolean onHoverEvent = super.onHoverEvent(motionEvent);
            if (actionMasked == 9 && !onHoverEvent) {
                this.f5098q = true;
            }
        }
        if (actionMasked == 10 || actionMasked == 3) {
            this.f5098q = false;
        }
        return true;
    }

    @Override // android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.f5097p = false;
        }
        if (!this.f5097p) {
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (actionMasked == 0 && !onTouchEvent) {
                this.f5097p = true;
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.f5097p = false;
        }
        return true;
    }

    public void setContentHeight(int i4) {
        this.f5095n = i4;
        requestLayout();
    }

    @Override // android.view.View
    public void setVisibility(int i4) {
        if (i4 != getVisibility()) {
            M.V v4 = this.f5096o;
            if (v4 != null) {
                v4.b();
            }
            super.setVisibility(i4);
        }
    }

    public AbstractC0692a(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        this.f5091j = new C0057a();
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(2130903042, typedValue, true) && typedValue.resourceId != 0) {
            this.f5092k = new ContextThemeWrapper(context, typedValue.resourceId);
        } else {
            this.f5092k = context;
        }
    }
}

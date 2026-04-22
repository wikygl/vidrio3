package J1;

import A1.C0116l;
import A1.C0120n;
import A1.C0124p;
import A1.Z0;
import A1.r;
import D1.E;
import E1.m;
import android.content.Context;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Xc;
import com.google.android.gms.internal.ads.dd;
import t1.InterfaceC0808j;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e extends FrameLayout {

    /* renamed from: j  reason: collision with root package name */
    public final FrameLayout f1240j;

    /* renamed from: k  reason: collision with root package name */
    public final dd f1241k;

    public e(Context context) {
        super(context);
        dd ddVar;
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(frameLayout);
        this.f1240j = frameLayout;
        if (isInEditMode()) {
            ddVar = null;
        } else {
            C0120n c0120n = C0124p.f.f162b;
            Context context2 = frameLayout.getContext();
            c0120n.getClass();
            ddVar = (dd) new C0116l(c0120n, this, frameLayout, context2).d(context2, false);
        }
        this.f1241k = ddVar;
    }

    public final View a(String str) {
        dd ddVar = this.f1241k;
        if (ddVar != null) {
            try {
                InterfaceC0374a F4 = ddVar.F(str);
                if (F4 != null) {
                    return (View) c2.b.p0(F4);
                }
            } catch (RemoteException e4) {
                m.e("Unable to call getAssetView on delegate", e4);
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public final void addView(View view, int i4, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i4, layoutParams);
        super.bringChildToFront(this.f1240j);
    }

    public final /* synthetic */ void b(InterfaceC0808j interfaceC0808j) {
        dd ddVar = this.f1241k;
        if (ddVar == null) {
            return;
        }
        try {
            if (interfaceC0808j instanceof Z0) {
                ((Z0) interfaceC0808j).getClass();
                ddVar.s4((Xc) null);
            } else if (interfaceC0808j == null) {
                ddVar.s4((Xc) null);
            } else {
                m.b("Use MediaContent provided by NativeAd.getMediaContent");
            }
        } catch (RemoteException e4) {
            m.e("Unable to call setMediaContent on delegate", e4);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public final void bringChildToFront(View view) {
        super.bringChildToFront(view);
        FrameLayout frameLayout = this.f1240j;
        if (frameLayout != view) {
            super.bringChildToFront(frameLayout);
        }
    }

    public final void c(View view, String str) {
        dd ddVar = this.f1241k;
        if (ddVar == null) {
            return;
        }
        try {
            ddVar.g4(new c2.b(view), str);
        } catch (RemoteException e4) {
            m.e("Unable to call setAssetView on delegate", e4);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public final boolean dispatchTouchEvent(MotionEvent motionEvent) {
        dd ddVar = this.f1241k;
        if (ddVar != null) {
            if (((Boolean) r.f168d.f171c.a(Gb.ma)).booleanValue()) {
                try {
                    ddVar.k1(new c2.b(motionEvent));
                } catch (RemoteException e4) {
                    m.e("Unable to call handleTouchEvent on delegate", e4);
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public a getAdChoicesView() {
        View a4 = a("3011");
        if (a4 instanceof a) {
            return (a) a4;
        }
        return null;
    }

    public final View getAdvertiserView() {
        return a("3005");
    }

    public final View getBodyView() {
        return a("3004");
    }

    public final View getCallToActionView() {
        return a("3002");
    }

    public final View getHeadlineView() {
        return a("3001");
    }

    public final View getIconView() {
        return a("3003");
    }

    public final View getImageView() {
        return a("3008");
    }

    public final b getMediaView() {
        View a4 = a("3010");
        if (a4 instanceof b) {
            return (b) a4;
        }
        if (a4 != null) {
            m.b("View is not an instance of MediaView");
            return null;
        }
        return null;
    }

    public final View getPriceView() {
        return a("3007");
    }

    public final View getStarRatingView() {
        return a("3009");
    }

    public final View getStoreView() {
        return a("3006");
    }

    @Override // android.view.View
    public final void onVisibilityChanged(View view, int i4) {
        super.onVisibilityChanged(view, i4);
        dd ddVar = this.f1241k;
        if (ddVar == null) {
            return;
        }
        try {
            ddVar.M1(new c2.b(view), i4);
        } catch (RemoteException e4) {
            m.e("Unable to call onVisibilityChanged on delegate", e4);
        }
    }

    @Override // android.view.ViewGroup
    public final void removeAllViews() {
        super.removeAllViews();
        addView(this.f1240j);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public final void removeView(View view) {
        if (this.f1240j == view) {
            return;
        }
        super.removeView(view);
    }

    public void setAdChoicesView(a aVar) {
        c(aVar, "3011");
    }

    public final void setAdvertiserView(View view) {
        c(view, "3005");
    }

    public final void setBodyView(View view) {
        c(view, "3004");
    }

    public final void setCallToActionView(View view) {
        c(view, "3002");
    }

    public final void setClickConfirmingView(View view) {
        dd ddVar = this.f1241k;
        if (ddVar == null) {
            return;
        }
        try {
            ddVar.H3(new c2.b(view));
        } catch (RemoteException e4) {
            m.e("Unable to call setClickConfirmingView on delegate", e4);
        }
    }

    public final void setHeadlineView(View view) {
        c(view, "3001");
    }

    public final void setIconView(View view) {
        c(view, "3003");
    }

    public final void setImageView(View view) {
        c(view, "3008");
    }

    public final void setMediaView(b bVar) {
        c(bVar, "3010");
        if (bVar == null) {
            return;
        }
        f fVar = new f(this);
        synchronized (bVar) {
            bVar.f1222n = fVar;
            if (bVar.f1219k) {
                b(bVar.f1218j);
            }
        }
        bVar.a(new E(this));
    }

    public void setNativeAd(c cVar) {
        dd ddVar = this.f1241k;
        if (ddVar == null) {
            return;
        }
        try {
            ddVar.y0(cVar.d());
        } catch (RemoteException e4) {
            m.e("Unable to call setNativeAd on delegate", e4);
        }
    }

    public final void setPriceView(View view) {
        c(view, "3007");
    }

    public final void setStarRatingView(View view) {
        c(view, "3009");
    }

    public final void setStoreView(View view) {
        c(view, "3006");
    }
}

package J1;

import D1.E;
import E1.m;
import android.os.RemoteException;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.gms.internal.ads.dd;
import t1.InterfaceC0808j;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b extends FrameLayout {

    /* renamed from: j  reason: collision with root package name */
    public InterfaceC0808j f1218j;

    /* renamed from: k  reason: collision with root package name */
    public boolean f1219k;

    /* renamed from: l  reason: collision with root package name */
    public ImageView.ScaleType f1220l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f1221m;

    /* renamed from: n  reason: collision with root package name */
    public f f1222n;

    /* renamed from: o  reason: collision with root package name */
    public E f1223o;

    public final synchronized void a(E e4) {
        this.f1223o = e4;
        if (this.f1221m) {
            ImageView.ScaleType scaleType = this.f1220l;
            dd ddVar = ((e) e4.f633j).f1241k;
            if (ddVar != null && scaleType != null) {
                try {
                    ddVar.C1(new c2.b(scaleType));
                } catch (RemoteException e5) {
                    m.e("Unable to call setMediaViewImageScaleType on delegate", e5);
                }
            }
        }
    }

    public InterfaceC0808j getMediaContent() {
        return this.f1218j;
    }

    public void setImageScaleType(ImageView.ScaleType scaleType) {
        dd ddVar;
        this.f1221m = true;
        this.f1220l = scaleType;
        E e4 = this.f1223o;
        if (e4 != null && (ddVar = ((e) e4.f633j).f1241k) != null && scaleType != null) {
            try {
                ddVar.C1(new c2.b(scaleType));
            } catch (RemoteException e5) {
                m.e("Unable to call setMediaViewImageScaleType on delegate", e5);
            }
        }
    }

    public void setMediaContent(InterfaceC0808j interfaceC0808j) {
        this.f1219k = true;
        this.f1218j = interfaceC0808j;
        f fVar = this.f1222n;
        if (fVar != null) {
            ((e) fVar.f1242j).b(interfaceC0808j);
        }
    }
}

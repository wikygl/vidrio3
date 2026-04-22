package C1;

import D1.AbstractC0200u;
import D1.t0;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import j$.util.concurrent.ConcurrentHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class p extends AbstractC0200u {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ s f384b;

    public /* synthetic */ p(s sVar) {
        this.f384b = sVar;
    }

    @Override // D1.AbstractC0200u
    public final void a() {
        BitmapDrawable bitmapDrawable;
        B0.v vVar = z1.p.f6575A.f6595u;
        s sVar = this.f384b;
        Bitmap bitmap = (Bitmap) ((ConcurrentHashMap) vVar.f293j).get(Integer.valueOf(sVar.f393l.x.f6553o));
        if (bitmap != null) {
            z1.g gVar = sVar.f393l.x;
            boolean z4 = gVar.f6551m;
            Activity activity = sVar.f392k;
            if (z4) {
                float f = gVar.f6552n;
                if (f > 0.0f && f <= 25.0f) {
                    try {
                        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                        Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap);
                        RenderScript create = RenderScript.create(activity);
                        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
                        Allocation createFromBitmap = Allocation.createFromBitmap(create, createScaledBitmap);
                        Allocation createFromBitmap2 = Allocation.createFromBitmap(create, createBitmap);
                        create2.setRadius(f);
                        create2.setInput(createFromBitmap);
                        create2.forEach(createFromBitmap2);
                        createFromBitmap2.copyTo(createBitmap);
                        bitmapDrawable = new BitmapDrawable(activity.getResources(), createBitmap);
                    } catch (RuntimeException unused) {
                        bitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
                    }
                    t0.f774l.post(new B.h(this, 1, bitmapDrawable));
                }
            }
            bitmapDrawable = new BitmapDrawable(activity.getResources(), bitmap);
            t0.f774l.post(new B.h(this, 1, bitmapDrawable));
        }
    }
}

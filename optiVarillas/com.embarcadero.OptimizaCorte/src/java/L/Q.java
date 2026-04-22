package l;

import android.content.Context;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class Q {

    /* renamed from: a  reason: collision with root package name */
    public final androidx.appcompat.view.menu.f f5053a;

    /* renamed from: b  reason: collision with root package name */
    public final View f5054b;

    /* renamed from: c  reason: collision with root package name */
    public final androidx.appcompat.view.menu.i f5055c;

    /* renamed from: d  reason: collision with root package name */
    public a f5056d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface a {
    }

    public Q(Context context, View view) {
        this.f5054b = view;
        androidx.appcompat.view.menu.f fVar = new androidx.appcompat.view.menu.f(context);
        this.f5053a = fVar;
        fVar.e = new O(this);
        androidx.appcompat.view.menu.i iVar = new androidx.appcompat.view.menu.i(2130903891, 0, context, view, fVar, false);
        this.f5055c = iVar;
        iVar.g = 0;
        iVar.k = new P(this);
    }
}

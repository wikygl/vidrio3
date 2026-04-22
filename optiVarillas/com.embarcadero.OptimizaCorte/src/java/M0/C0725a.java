package m0;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import m0.AbstractC0731g;
import q0.InterfaceC0767b;

/* renamed from: m0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0725a {

    /* renamed from: a  reason: collision with root package name */
    public final InterfaceC0767b.c f5279a;

    /* renamed from: b  reason: collision with root package name */
    public final Context f5280b;

    /* renamed from: c  reason: collision with root package name */
    public final String f5281c;

    /* renamed from: d  reason: collision with root package name */
    public final AbstractC0731g.d f5282d;

    /* renamed from: e  reason: collision with root package name */
    public final List<AbstractC0731g.b> f5283e;
    public final Executor f;

    /* renamed from: g  reason: collision with root package name */
    public final Executor f5284g;

    /* renamed from: h  reason: collision with root package name */
    public final boolean f5285h;

    /* renamed from: i  reason: collision with root package name */
    public final boolean f5286i;

    public C0725a(Context context, String str, InterfaceC0767b.c cVar, AbstractC0731g.d dVar, ArrayList arrayList, boolean z4, AbstractC0731g.c cVar2, Executor executor, Executor executor2, boolean z5, boolean z6) {
        this.f5279a = cVar;
        this.f5280b = context;
        this.f5281c = str;
        this.f5282d = dVar;
        this.f = executor;
        this.f5284g = executor2;
        this.f5285h = z5;
        this.f5286i = z6;
    }

    public final boolean a(int i4, int i5) {
        if (i4 > i5 && this.f5286i) {
            return false;
        }
        return this.f5285h;
    }
}

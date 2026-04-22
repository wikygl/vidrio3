package R;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build;
import android.view.inputmethod.InputContentInfo;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class g {

    /* renamed from: a  reason: collision with root package name */
    public final c f2033a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface c {
        Object a();

        Uri b();

        void c();

        Uri d();

        ClipDescription getDescription();
    }

    public g(Uri uri, ClipDescription clipDescription, Uri uri2) {
        if (Build.VERSION.SDK_INT >= 25) {
            this.f2033a = new a(uri, clipDescription, uri2);
        } else {
            this.f2033a = new b(uri, clipDescription, uri2);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a implements c {

        /* renamed from: a  reason: collision with root package name */
        public final InputContentInfo f2034a;

        public a(Object obj) {
            this.f2034a = (InputContentInfo) obj;
        }

        @Override // R.g.c
        public final Object a() {
            return this.f2034a;
        }

        @Override // R.g.c
        public final Uri b() {
            return this.f2034a.getContentUri();
        }

        @Override // R.g.c
        public final void c() {
            this.f2034a.requestPermission();
        }

        @Override // R.g.c
        public final Uri d() {
            return this.f2034a.getLinkUri();
        }

        @Override // R.g.c
        public final ClipDescription getDescription() {
            return this.f2034a.getDescription();
        }

        public a(Uri uri, ClipDescription clipDescription, Uri uri2) {
            this.f2034a = new InputContentInfo(uri, clipDescription, uri2);
        }
    }

    public g(a aVar) {
        this.f2033a = aVar;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class b implements c {

        /* renamed from: a  reason: collision with root package name */
        public final Uri f2035a;

        /* renamed from: b  reason: collision with root package name */
        public final ClipDescription f2036b;

        /* renamed from: c  reason: collision with root package name */
        public final Uri f2037c;

        public b(Uri uri, ClipDescription clipDescription, Uri uri2) {
            this.f2035a = uri;
            this.f2036b = clipDescription;
            this.f2037c = uri2;
        }

        @Override // R.g.c
        public final Object a() {
            return null;
        }

        @Override // R.g.c
        public final Uri b() {
            return this.f2035a;
        }

        @Override // R.g.c
        public final Uri d() {
            return this.f2037c;
        }

        @Override // R.g.c
        public final ClipDescription getDescription() {
            return this.f2036b;
        }

        @Override // R.g.c
        public final void c() {
        }
    }
}

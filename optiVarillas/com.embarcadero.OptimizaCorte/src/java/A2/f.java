package a2;

import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class f {
    public static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    @ResultIgnorabilityUnspecified
    @Deprecated
    public static long b(InputStream inputStream, OutputStream outputStream, boolean z4) {
        byte[] bArr = new byte[1024];
        long j4 = 0;
        while (true) {
            try {
                int read = inputStream.read(bArr, 0, 1024);
                if (read == -1) {
                    break;
                }
                j4 += read;
                outputStream.write(bArr, 0, read);
            } catch (Throwable th) {
                if (z4) {
                    a(inputStream);
                    try {
                        outputStream.close();
                    } catch (IOException unused) {
                    }
                }
                throw th;
            }
        }
        if (z4) {
            try {
                inputStream.close();
            } catch (IOException unused2) {
            }
            try {
                outputStream.close();
            } catch (IOException unused3) {
            }
        }
        return j4;
    }
}

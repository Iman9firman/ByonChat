package com.byonchat.android.personalRoom.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Lukmanpryg on 4/25/2016.
 */
@SuppressWarnings("deprecation")
public class AndroidMultiPartEntity  {

    public static interface ProgressListener {
        void transferred(long num);
    }

    public static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;
        private long transferred;

        public CountingOutputStream(final OutputStream out,
                                    final ProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);
        }
    }
}

package com.github.tachesimazzoca.imgconv;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestUtils {
    public static byte[] readFileToByteArray(File f) throws IOException {
        FileInputStream input = new FileInputStream(f);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            copyLarge(input, output);
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return output.toByteArray();
    }

    public static String readFileToString(File f) throws IOException {
        FileInputStream input = new FileInputStream(f);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            copyLarge(input, output);
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return output.toString();
    }

    private static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}

package org.swiftleap.common.security.impl;


import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

//env.put("java.naming.ldap.factory.socket", "org.swiftleap.common.security.impl.JndiTrustAllSocketFactory");
public class JndiTrustAllSocketFactory extends SocketFactory {
    private static final AtomicReference<JndiTrustAllSocketFactory> defaultFactory = new AtomicReference<>();

    private SSLSocketFactory sf;

    class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            // do nothing
        }

        public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            // do nothing
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    }

    public JndiTrustAllSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, new TrustManager[] { new TrustAllManager() }, new SecureRandom());
        sf = ctx.getSocketFactory();
    }

    public static SocketFactory getDefault() {
        final JndiTrustAllSocketFactory value = defaultFactory.get();
        if (value == null) {
            try {
                defaultFactory.compareAndSet(null, new JndiTrustAllSocketFactory());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return defaultFactory.get();
        }
        return value;
    }

    @Override
    public Socket createSocket(final String s, final int i) throws IOException {
        return sf.createSocket(s, i);
    }

    @Override
    public Socket createSocket(final String s, final int i, final InetAddress inetAddress, final int i1) throws IOException {
        return sf.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i) throws IOException {
        return sf.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(final InetAddress inetAddress, final int i, final InetAddress inetAddress1, final int i1) throws IOException {
        return sf.createSocket(inetAddress, i, inetAddress1, i1);
    }
}
package org.alexdalton.jira.utils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.alexdalton.jira.JiraApp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

public class ConnectionClient extends DefaultHttpClient {
    public ConnectionClient(Credentials cred) {
        super();
        if (cred != null)
            setCredentials(cred);
        HttpConnectionParams.setConnectionTimeout(this.getParams(), 15000);
    }

    public ConnectionClient(Credentials cred, int port) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        super();
        if (JiraApp.get().allowAllSSL)
            registerTrustAllScheme(port);
        if (cred != null)
            setCredentials(cred);
    }

    private void registerTrustAllScheme(int port) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        TrustAllSSLSocketFactory tasslf = new TrustAllSSLSocketFactory();
        Scheme sch = new Scheme("https", tasslf, port);
        getConnectionManager().getSchemeRegistry().register(sch);
    }

    private void setCredentials(Credentials cred) {
        BasicCredentialsProvider cP = new BasicCredentialsProvider();
        cP.setCredentials(AuthScope.ANY, cred);
        setCredentialsProvider(cP);
    }
}

/*******************************************************************************
 * Copyright 2012 Alexandre d'Alton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openjira.jira.utils;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.openjira.jira.JiraApp;

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

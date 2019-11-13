/*
 * RESTHeart Security
 * 
 * Copyright (C) SoftInstigate Srl
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.restheart.security.plugins.authenticators;

import com.google.common.collect.Sets;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.DigestCredential;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.util.HexConverter;
import org.restheart.security.ConfigurationException;
import org.restheart.security.plugins.Authenticator;
import org.restheart.security.plugins.FileConfigurablePlugin;
import org.restheart.security.utils.LambdaUtils;

import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.restheart.security.plugins.ConfigurablePlugin.argValue;

/**
 *
 * Authenticator with permission defined in a yml configuration file
 *
 * supports PasswordCredentials and DigestCredentials
 *
 * @author Andrea Di Cesare {@literal <andrea@softinstigate.com>}
 */
public class TapisSimpleFileAuthenticator
        extends FileConfigurablePlugin
        implements Authenticator {

    private final Map<String, PwdCredentialAccount> accounts = new HashMap<>();

    /**
     *
     * @param arguments
     * @throws FileNotFoundException
     */
    public TapisSimpleFileAuthenticator(String name, Map<String, Object> arguments)
            throws FileNotFoundException, ConfigurationException {
        init(arguments, "users");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Consumer<? super Map<String, Object>> consumeConfiguration() {
        return u -> {
            try {
                String userid = argValue(u, "userid");
                String _password = argValue(u, "password");
                char[] password = ((String) _password).toCharArray();

                List _roles = argValue(u, "roles");

                if (((Collection<?>) _roles).stream().anyMatch(i -> !(i instanceof String))) {
                    throw new IllegalArgumentException(
                            "wrong configuration file format. a roles entry is wrong. they all must be strings");
                }

                Set<String> roles = Sets.newLinkedHashSet((Collection<String>) _roles);

                PwdCredentialAccount a = new PwdCredentialAccount(userid, password, roles);

                this.accounts.put(userid, a);
            } catch (ConfigurationException pce) {
                LambdaUtils.throwsSneakyExcpetion(pce);
            }
        };
    }

    @Override
    public Account verify(Account account) {
        return account;
    }

    @Override
    public Account verify(String id, Credential credential) {
        final Account account = accounts.get(id);
        return account != null && verifyCredential(account, credential) ? account : null;
    }

    @Override
    public Account verify(Credential credential) {
        // Auto-generated method stub
        return null;
    }

    private boolean verifyCredential(Account account, Credential credential) {
        if (account instanceof PwdCredentialAccount) {
            if (credential instanceof PasswordCredential) {
                return verifyPasswordCredential(account, credential);
            } else if (credential instanceof DigestCredential) {
                return verifyDigestCredential(account, credential);
            }
        }

        return false;
    }

    private boolean verifyPasswordCredential(Account account, Credential credential) {
        char[] password = ((PasswordCredential) credential).getPassword();
        char[] expectedPassword = accounts.get(account.getPrincipal().getName()).getCredentials().getPassword();

        return Arrays.equals(password, expectedPassword);
    }

    private boolean verifyDigestCredential(Account account, Credential credential) {
        try {
            DigestCredential dc = (DigestCredential) credential;

            MessageDigest digest = dc.getAlgorithm().getMessageDigest();

            String expectedPassword = new String(
                    accounts.get(account.getPrincipal().getName()).getCredentials().getPassword());

            digest.update(account.getPrincipal().getName().getBytes(UTF_8));
            digest.update((byte) ':');
            digest.update(dc.getRealm().getBytes(UTF_8));
            digest.update((byte) ':');
            digest.update(expectedPassword.getBytes(UTF_8));

            byte[] ha1 = HexConverter.convertToHexBytes(digest.digest());

            return dc.verifyHA1(ha1);
        } catch (NoSuchAlgorithmException ne) {
            return false;
        }
    }
}

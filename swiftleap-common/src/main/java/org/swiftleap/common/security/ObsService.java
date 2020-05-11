package org.swiftleap.common.security;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ObsService {
    String obfuscatePassword(String password) throws GeneralSecurityException, IOException;

    String unObfuscatePassword(String obfuscated) throws GeneralSecurityException, IOException;
}

/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;

/**
 * Created by byang059 on 5/20/20
 */

public class KeyGenerator {

    @Test
    public void generateKey() throws IOException {
        byte[] encoded = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        IOUtils.write(encoded,new FileOutputStream(new File("./private.key")));
    }

}

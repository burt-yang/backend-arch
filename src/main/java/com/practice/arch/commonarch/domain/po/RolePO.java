/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.domain.po;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by byang059 on 5/12/20
 */
@Data
public class RolePO implements GrantedAuthority {
    String name;
    @Override
    public String getAuthority() {
        return name;
    }
}

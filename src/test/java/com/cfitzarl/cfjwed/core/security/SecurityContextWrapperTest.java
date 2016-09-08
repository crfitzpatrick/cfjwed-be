package com.cfitzarl.cfjwed.core.security;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SecurityContextWrapperTest {

    @Test
    public void testGetIdReturnsUUIDOfAccount() throws Exception {
        UUID uuid = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(uuid, "creds"));

        assertEquals(uuid, SecurityContextWrapper.getId());
    }
}
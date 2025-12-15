package org.example.DTO;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserIdsRequestTest {

    @Test
    void testUserIdsRequest_GetSetIds() {
        UserIdsRequest request = new UserIdsRequest();
        List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);

        request.setIds(ids);

        assertEquals(ids, request.getIds());
        assertEquals(5, request.getIds().size());
        assertEquals(1, request.getIds().get(0));
        assertEquals(5, request.getIds().get(4));
    }

    @Test
    void testUserIdsRequest_EmptyList() {
        UserIdsRequest request = new UserIdsRequest();

        request.setIds(Arrays.asList());

        assertNotNull(request.getIds());
        assertTrue(request.getIds().isEmpty());
    }

    @Test
    void testUserIdsRequest_NullList() {
        UserIdsRequest request = new UserIdsRequest();

        request.setIds(null);

        assertNull(request.getIds());
    }

    @Test
    void testUserIdsRequest_ListOperations() {
        UserIdsRequest request = new UserIdsRequest();
        List<Integer> ids = Arrays.asList(10, 20, 30);

        request.setIds(ids);

        assertTrue(request.getIds().contains(20));
        assertFalse(request.getIds().contains(40));
        assertEquals(3, request.getIds().size());
    }
}
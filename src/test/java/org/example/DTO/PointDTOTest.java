package org.example.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointDTOTest {

    @Test
    void testPointDTO_NoArgsConstructor() {
        PointDTO point = new PointDTO();

        point.setId(1);
        point.setFunctionId(100);
        point.setXValue(2.5);
        point.setYValue(3.7);

        assertEquals(1, point.getId());
        assertEquals(100, point.getFunctionId());
        assertEquals(2.5, point.getXValue());
        assertEquals(3.7, point.getYValue());
    }

    @Test
    void testPointDTO_AllArgsConstructor() {
        PointDTO point = new PointDTO(1, 100, 2.5, 3.7);

        assertEquals(1, point.getId());
        assertEquals(100, point.getFunctionId());
        assertEquals(2.5, point.getXValue());
        assertEquals(3.7, point.getYValue());
    }

    @Test
    void testPointDTO_ToString() {
        PointDTO point = new PointDTO(1, 100, 2.5, 3.7);

        String result = point.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("functionId=100"));
        assertTrue(result.contains("xValue=2.5"));
        assertTrue(result.contains("yValue=3.7"));
    }

    @Test
    void testPointDTO_NullValues() {
        PointDTO point = new PointDTO();

        point.setId(null);
        point.setFunctionId(null);
        point.setXValue(null);
        point.setYValue(null);

        assertNull(point.getId());
        assertNull(point.getFunctionId());
        assertNull(point.getXValue());
        assertNull(point.getYValue());
    }

    @Test
    void testPointDTO_DecimalValues() {
        PointDTO point = new PointDTO();

        point.setXValue(0.0);
        point.setYValue(-1.5);

        assertEquals(0.0, point.getXValue());
        assertEquals(-1.5, point.getYValue());
    }
}

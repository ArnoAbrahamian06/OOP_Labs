package org.example.mapper;

import org.example.DTO.PointDTO;
import org.example.models.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointMapperTest {

    private Point point;
    private PointDTO pointDTO;

    @BeforeEach
    void setUp() {
        point = new Point();
        point.setId(1);
        point.setFunctionId(100);
        point.setXValue(2.5);
        point.setYValue(3.7);

        pointDTO = new PointDTO(1, 100, 2.5, 3.7);
    }

    @Test
    void testToDTO_ValidPoint() {
        PointDTO result = PointMapper.toDTO(point);

        assertNotNull(result);
        assertEquals(point.getId(), result.getId());
        assertEquals(point.getFunctionId(), result.getFunctionId());
        assertEquals(point.getXValue(), result.getXValue());
        assertEquals(point.getYValue(), result.getYValue());
    }

    @Test
    void testToDTO_NullPoint() {
        PointDTO result = PointMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    void testToEntity_ValidPointDTO() {
        Point result = PointMapper.toEntity(pointDTO);

        assertNotNull(result);
        assertEquals(pointDTO.getId(), result.getId());
        assertEquals(pointDTO.getFunctionId(), result.getFunctionId());
        assertEquals(pointDTO.getXValue(), result.getXValue());
        assertEquals(pointDTO.getYValue(), result.getYValue());
    }

    @Test
    void testToEntity_NullPointDTO() {
        Point result = PointMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToEntity_WithNullValues() {
        PointDTO dtoWithNulls = new PointDTO(null, null, null, null);

        Point result = PointMapper.toEntity(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getFunctionId());
        assertNull(result.getXValue());
        assertNull(result.getYValue());
    }

    @Test
    void testToDTO_WithNullValues() {
        Point pointWithNulls = new Point();
        pointWithNulls.setId(null);
        pointWithNulls.setFunctionId(null);
        pointWithNulls.setXValue(null);
        pointWithNulls.setYValue(null);

        PointDTO result = PointMapper.toDTO(pointWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getFunctionId());
        assertNull(result.getXValue());
        assertNull(result.getYValue());
    }
}

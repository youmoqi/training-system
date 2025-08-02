package com.training.service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TrainingCertificateServiceTest {

    @Autowired
    private TrainingCertificateService certificateService;

    @Test
    public void testGetCertificatesWithPagination() {
        // 测试分页查询
        Map<String, Object> result = certificateService.getCertificatesWithPagination(0, 10, null, null, null);

        assertNotNull(result);
        assertTrue(result.containsKey("certificates"));
        assertTrue(result.containsKey("total"));
        assertTrue(result.containsKey("totalPages"));
        assertTrue(result.containsKey("currentPage"));
        assertTrue(result.containsKey("size"));

        assertEquals(0, result.get("currentPage"));
        assertEquals(10, result.get("size"));
    }

    @Test
    public void testGetCertificatesWithFilters() {
        // 测试带筛选条件的分页查询
        Map<String, Object> result = certificateService.getCertificatesWithPagination(0, 10, "EXPLOSIVE_USER", true, "test");

        assertNotNull(result);
        assertTrue(result.containsKey("certificates"));
    }
}

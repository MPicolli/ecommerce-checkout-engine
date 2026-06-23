package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Cupom;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CupomRepositoryTest {

    @Test
    void deveBuscarTodosOsCuponsDoBancoSimulado() {
        CupomRepository repo = new CupomRepository();
        List<Cupom> cupons = repo.buscarTodos();
        
        assertNotNull(cupons);
        assertEquals(2, cupons.size());
        assertEquals("CUPOM10", cupons.get(0).getCodigo());
    }
}
package com.albatross.visualivr.project.action;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class DeleteOperationTest {
    
    private DeleteOperation delete;

    @Before
    public void setUp() {
      delete  = new DeleteOperation();
    }

    /**
     * Test of getMetadataFiles method, of class DeleteOperation.
     */
    @Test
    public void testGetMetadataFiles() {
      assertSame(delete.getMetadataFiles(), Collections.emptyList());
    }

    /**
     * Test of getDataFiles method, of class DeleteOperation.
     */
    @Test
    public void testGetDataFiles() {
      assertSame(delete.getDataFiles(), Collections.emptyList());
    }

}
package com.albatross.visualivr.project.action;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class CopyOperationTest {
    
    private CopyOperation copy;

    @Before
    public void setUp() {
       copy  = new CopyOperation();
    }

    /**
     * Test of getMetadataFiles method, of class DeleteOperation.
     */
    @Test
    public void testGetMetadataFiles() {
      assertSame(copy.getMetadataFiles(), Collections.emptyList());
    }

    /**
     * Test of getDataFiles method, of class DeleteOperation.
     */
    @Test
    public void testGetDataFiles() {
      assertSame(copy.getDataFiles(), Collections.emptyList());
    }

}
package com.telmi.msc.fsadapter;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class FooTest {

    @Test
    public void foo() {

        File file = new File("/tmp/foo", "bar/foobar.txt");
        Assert.assertEquals("/tmp/foo/bar/foobar.txt", file.getAbsolutePath());

    }

}

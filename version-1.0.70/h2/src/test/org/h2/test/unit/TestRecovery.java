/*
 * Copyright 2004-2008 H2 Group. Licensed under the H2 License, Version 1.0
 * (license2)
 * Initial Developer: H2 Group
 */
package org.h2.test.unit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.h2.test.TestBase;
import org.h2.tools.DeleteDbFiles;

/**
 * Tests database recovery.
 */
public class TestRecovery extends TestBase {

    public void test() throws Exception {
        DeleteDbFiles.execute(baseDir, "recovery", true);
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:" + baseDir + "/recovery;write_delay=0";
        Connection conn1 = DriverManager.getConnection(url, "sa", "sa");
        Statement stat1 = conn1.createStatement();
        Connection conn2 = DriverManager.getConnection(url, "sa", "sa");
        Statement stat2 = conn2.createStatement();
        stat1.execute("create table test as select * from system_range(1, 100)");
        stat1.execute("create table abc(id int)");
        conn2.setAutoCommit(false);
        // this is not committed
        // recovery might try to roll back this
        stat2.execute("delete from test");
        // overwrite the data of test
        stat1.execute("insert into abc select * from system_range(1, 100)");
        stat1.execute("shutdown immediately");
        // Recover.execute("data", null);
        Connection conn = DriverManager.getConnection(url, "sa", "sa");
        conn.close();
    }

}
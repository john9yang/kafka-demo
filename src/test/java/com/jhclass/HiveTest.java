package com.jhclass;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HiveTest {
    private static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static String CONNECTION_URL =
            "jdbc:hive2://sandbox-hdp.hortonworks.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";

    static{
        try {
            Class.forName(JDBC_DRIVER);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void showTables(){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try{
            connection = DriverManager.getConnection(CONNECTION_URL,"hive","hive");
            ps = connection.prepareStatement("show databases");
            rs = ps.executeQuery();
            while ( rs.next() ){
                System.out.println("tableName:"+rs.getString(1));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                if ( rs != null ){
                    rs.close();
                }

                if ( ps != null ){
                    ps.close();
                }

                if ( connection != null ){
                    connection.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}

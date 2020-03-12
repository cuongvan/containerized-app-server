/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appinfo.DBAppInfoDAO;
import helpers.DBHelper;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author cuong
 */
public class DBAppInfoDAOTest {
    DBAppInfoDAO dao = new DBAppInfoDAO(DBHelper.getPool());
    
    @Before
    public void setUp() {
        DBHelper.truncateTables();
    }

    @Test
    public void add_then_retrieve() {
        AppInfo insert = newApp();
        
        String appId = dao.createApp(insert);
        
        AppInfo getApp = dao.getById(appId);
        assertEquals(createdApp(insert), getApp);
    }
    
    @Test
    public void delete_app() {
        AppInfo app = newApp();
        
        String appId = dao.createApp(app);
        dao.deleteById(appId);
        assertNull(dao.getById(app.getAppId()));
        
    }
    
    public static AppInfo newApp() {
        return AppInfo.builder()
            .withAppName("show number of rows in csv resource")
            .withType(AppType.BATCH)
            .withLanguage(SupportLanguage.PYTHON)
            .build();
    }
    
    public static AppInfo createdApp(AppInfo app) {
        return AppInfo.builder(app).withStatus(AppStatus.CREATED).build();
    }
}

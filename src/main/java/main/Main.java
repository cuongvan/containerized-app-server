package main;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import common.AppConfig;
import externalapi.appcall.DBAppCallDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.appcall.AppCallDAO;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.DBAppInfoDAO;
import externalapi.DBConnectionPool;
import helpers.DBHelper;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Server server;
    private static Injector injector;
    
    public static void main(String... args) throws Exception {
        DBHelper.createTables();
        injector = initialize();
        {
            ContainerFinishWatcher watchingContainerWorker = injector.getInstance(ContainerFinishWatcher.class);
            watchingContainerWorker.runForever();
        }
        
        server = WebServiceServer.createServer(injector);
        server.start();
        logger.info("Application started");
    }
    
    public static Injector initialize() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(AppConfig.class).toProvider(() -> AppConfig.Inst);
                bind(DBConnectionPool.class).toProvider(() -> DBHelper.getPool());
                bind(AppInfoDAO.class).to(DBAppInfoDAO.class);
                bind(AppCallDAO.class).to(DBAppCallDAO.class);
            }
        });
    }

    public static void stop() throws Exception {
        logger.info("Stop server");
        server.stop();
        injector.getInstance(ContainerFinishWatcher.class).stop();
    }
    
    public static void createAppBuildDirs(){
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_BUILD_DIR))) {
            new File(AppConfig.Inst.APP_BUILD_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_BUILD_FAILED_DIR))) {
            new File(AppConfig.Inst.APP_BUILD_FAILED_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_INPUT_FILES_DIR))) {
            new File(AppConfig.Inst.APP_INPUT_FILES_DIR).mkdirs();
        }
    }
}


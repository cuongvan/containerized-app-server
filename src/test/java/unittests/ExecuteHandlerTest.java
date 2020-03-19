package unittests;

import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import handlers.AppNotBuiltYet;
import handlers.AppNotFound;
import handlers.ExecuteHandler;
import java.io.IOException;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteHandlerTest {
    @Mock DockerAdapter docker;
    @Mock AppInfoDAO appInfoDAO;
    @Mock AppCallDAO appCallDAO;
    
    ExecuteHandler handler;

    @Before
    public void setup() {
        handler = new ExecuteHandler(docker, appInfoDAO, appCallDAO);
    }

    @Test
    public void executeFoundApp() throws IOException, AppNotBuiltYet, AppNotFound {
        when(appInfoDAO.getById("app-id")).thenReturn(appWithImageId("app-image"));
        
        HashMap<String, byte[]> files = new HashMap<String, byte[]>() {{
            put("datasetId", "123".getBytes());
            put("dataFile", "file content".getBytes());
        }};
        
        handler.execute("app-id", "user-id", files);
        verify(appCallDAO).createNewCall(anyString(), eq("app-id"), eq("user-id"), anyList(), anyList());
    }

    private static AppInfo appWithImageId(String imageId) {
        return new AppInfo().withImageId(imageId);
    }
}

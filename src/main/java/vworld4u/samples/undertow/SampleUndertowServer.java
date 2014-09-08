package vworld4u.samples.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.core.DeploymentManagerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.servlet.ServletException;
import javax.swing.plaf.synth.Region;

public class SampleUndertowServer {
	public static final String APP_URL_SUFFIX = "/";
	private static final int SERVER_PORT = 9010;

	public static void main(String[] args) {
		try {
			String filePath = SampleUndertowServer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			System.out.println("File path of Jar  = " + filePath + "  " + filePath.lastIndexOf('/'));
			filePath = filePath.substring(0, filePath.lastIndexOf('/'));
			File base = new File(filePath);
			System.out.println("File Based Resource Manager Base = " + base);
			// FileResourceManager resourceManager = new
			// FileResourceManager(base, 8092);
			ClassPathResourceManager resourceManager = new ClassPathResourceManager(UndertowServlet.class.getClassLoader(), UndertowServlet.class.getPackage());
			ResourceHandler resourceHandler = Handlers.resource(resourceManager).addWelcomeFiles("index.html").setDirectoryListingEnabled(true);
//			try {
//				Resource res = resourceManager.getResource("index.html");
//				System.out.println("File Resource (index.html) : " + res + " File = " + res.getFile());
//				File file = res.getFile();
//				Path path = FileSystems.getDefault().getPath(file.getPath());
//				byte[] bytes = Files.readAllBytes(path);
//				String str = new String(bytes);
//				System.out.println(" Read Bytes = " + str);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			// DirectBufferCache dataCache = new DirectBufferCache(10, 10,
			// 10000000);
			// CachingResourceManager resMgr = new CachingResourceManager(100,
			// 1000000L, dataCache , resourceManager, 30);
			DeploymentInfo servletBuilder = new DeploymentInfo().setClassLoader(ServletContainer.class.getClassLoader()).setContextPath(APP_URL_SUFFIX).setDeploymentName("test.war").addListener(new ListenerInfo(MySessionListener.class))
					.addServletExtension(new NonBlockingHandlerExtension()).setResourceManager(resourceManager).addServlets(Servlets.servlet("TestServlet", UndertowServlet.class).addInitParam("message", "Hello World").addMapping("test/*"));

			ServletContainer servletContainer = ServletContainer.Factory.newInstance();
			servletContainer.addDeployment(servletBuilder);
			DeploymentManager manager = new DeploymentManagerImpl(servletBuilder, servletContainer);
			manager.deploy();
			System.out.println(" Deployment := " + manager.getDeployment());
			HttpHandler servletHandler = manager.start();
			PathHandler hanlder = Handlers.path(Handlers.redirect(APP_URL_SUFFIX));
			PathHandler path = hanlder.addPrefixPath(APP_URL_SUFFIX, resourceHandler).addPrefixPath(APP_URL_SUFFIX + "services", servletHandler);
			Undertow server = Undertow.builder().addHttpListener(SERVER_PORT, "localhost").setHandler(path).build();
			server.start();
		} catch (ServletException e) {
			e.printStackTrace();
		} finally {
			// if (queueClient != null) {
			// System.out.println("Shutting down Queue Client ...");
			// queueClient.shutdown();
			// }
		}
		System.out.println("Completed the Server setup.. Waiting for Connections .... ! ");
	}

}

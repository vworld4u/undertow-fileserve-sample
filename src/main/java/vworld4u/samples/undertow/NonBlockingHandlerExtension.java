package vworld4u.samples.undertow;

import io.undertow.predicate.Predicates;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;

import javax.servlet.ServletContext;

public class NonBlockingHandlerExtension implements ServletExtension {

	@Override
	public void handleDeployment(DeploymentInfo deploymentInfo,
			ServletContext servletContext) {
		System.out.println("handleDeployment : I will handle static file serving !");
		deploymentInfo.addInitialHandlerChainWrapper(new HandlerWrapper() {
			@Override
			public HttpHandler wrap(final HttpHandler handler) {
				System.out.println(" Initializing Deployment information to handle async handling of static files !");
				final ResourceHandler resourceHandler = new ResourceHandler()
						.setResourceManager(deploymentInfo.getResourceManager());

				PredicateHandler predicateHandler = new PredicateHandler(
						Predicates.suffixes(".html", ".css", ".js", ".txt",
								".jpg", ".png"), resourceHandler, handler);

				return predicateHandler;
			}
		});
	}
}

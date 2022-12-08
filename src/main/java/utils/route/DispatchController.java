package utils.route;

import context.ApplicationContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.ServletContextAware;

import java.io.IOException;

@RequiredArgsConstructor
public class DispatchController implements  ServletContextAware {

	private static final String VIEW_DIRECTORY = "views";

	@Setter
	private ServletContext servletContext;


	public void changeRouteMethod(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull HttpMethod method) throws ServletException, IOException {

		final var requestDispatcher = this.servletContext
				.getRequestDispatcher(request.getRequestURI().substring(request.getContextPath().length()));
		final var requestWrapper = new HttpServletRequestWrapper(request) {

			@Override
			public String getMethod() {
				return method.toString();
			}
		};
		requestDispatcher.forward(requestWrapper, response);
	}

	public String redirectToRoute(@NonNull String routeLink) {
		if (routeLink.startsWith("/")) {
			final var request = ApplicationContext.getRequest();
			final var contextPath = request.getServletContext().getContextPath();
			if (!contextPath.isEmpty() && routeLink.startsWith(contextPath))
				routeLink = routeLink.substring(contextPath.length());
		}
		return "redirect:" + routeLink;
	}

}
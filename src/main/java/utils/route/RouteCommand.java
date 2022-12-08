package utils.route;

import context.ApplicationContext;
import lombok.NonNull;

public class RouteCommand {

	private final String link;

	public RouteCommand(@NonNull String link, boolean relative) {
		if (relative) {
			final var request = ApplicationContext.getRequest();
			final var requestMapping = request.getServletPath();
			final var uri = request.getRequestURI();

			final var startIndex = uri.indexOf(requestMapping);
			final var endIndex = uri.lastIndexOf('/');
			this.link = ((endIndex > startIndex) ? uri.substring(0, endIndex) : uri) + link;
			return;
		}
		this.link = link;
	}

}
package context;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationContext implements ApplicationContextAware {


	private static org.springframework.context.ApplicationContext delegate;

	public static boolean isAvailable() {
		return (delegate != null);
	}

	protected static org.springframework.context.ApplicationContext getDelegate() {
		if (!isAvailable())
			throw new IllegalStateException("Application context is not available.");

		return delegate;
	}

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
		delegate = applicationContext;
	}

	public static Path getClassExecutionPath(@NonNull Class<?> c) {
		try {
			return new File(c.getProtectionDomain().getCodeSource().getLocation()
					.toURI()).toPath();
		} catch (final URISyntaxException ex) {
			throw new IllegalStateException(ex);
		}
	}


	public static Collection<?> getConfigurations() {
		return getDelegate().getBeansWithAnnotation(Configuration.class)
				.values();
	}

	public static boolean containsBean(@NonNull String name) {
		return getDelegate().containsBean(name);
	}

	public static Object getBean(@NonNull String name) {
		return getDelegate().getBean(name);
	}

	public static <T> T getBean(@NonNull Class<T> type) {
		return getDelegate().getBean(type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(@NonNull Class<T> type, Class<?>... generics) {
		final var delegate = getDelegate();
		final var resolvedType = ResolvableType.forClassWithGenerics(type, generics);
		final var beanNames = delegate.getBeanNamesForType(resolvedType);
		if (beanNames.length == 0)
			throw new NoSuchBeanDefinitionException(type);

		return (T) delegate.getBean(beanNames[0]);
	}

	public static Object getProxyTarget(Object proxy) {
		if (!AopUtils.isAopProxy(proxy))
			throw new IllegalArgumentException("A proxy object was expected.");

		try {
			final var targetSource = ((Advised) proxy).getTargetSource();
			return targetSource.getTarget();
		} catch (final Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static List<Resource> getResources(String locationPattern) throws IOException {
		return List.of(getDelegate().getResources(locationPattern));
	}

	public static ServletContext getServletContext() {
		if (!(delegate instanceof WebApplicationContext))
			throw new IllegalStateException("Cannot be used outside of a web context.");

		return ((WebApplicationContext) delegate).getServletContext();
	}

	public static URL getRootUrl() {
		final var request = getRequest();
		final var requestUrl = request.getRequestURL().toString();
		try {
			return new URL(
					requestUrl.substring(0, requestUrl.length() - request.getRequestURI().length())
							+ request.getServletContext().getContextPath());
		} catch (final MalformedURLException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static HttpServletRequest getRequest() {
		return getRequestAttributes().getRequest();
	}

	public static HttpServletResponse getResponse() {
		return getRequestAttributes().getResponse();
	}

	private static ServletRequestAttributes getRequestAttributes() {
		final var requestAttributes = RequestContextHolder.getRequestAttributes();
		if (!(requestAttributes instanceof ServletRequestAttributes))
			throw new IllegalStateException("Cannot be used outside of a web context.");

		return (ServletRequestAttributes) requestAttributes;
	}

	public static Map<String, Object> getProperties() {
		return findProperties(propertyName -> true);
	}

	public static Map<String, Object> findProperties(@NonNull Predicate<String> predicate) {
		final var environment = ApplicationContext.getBean(ConfigurableEnvironment.class);
		final var propertyMap = new LinkedHashMap<String, Object>();

		for (final var propertySource : environment.getPropertySources()) {
			if (!(propertySource instanceof EnumerablePropertySource))
				continue;

			final var enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
			for (final var propertyName : enumerablePropertySource.getPropertyNames())
				if (predicate.test(propertyName))
					propertyMap.put(propertyName, environment.getProperty(propertyName));
		}
		return propertyMap;
	}

	public static Set<String> getActiveProfiles() {
		final var environment = getBean(ConfigurableEnvironment.class);
		return Set.of(environment.getActiveProfiles());
	}

	public static boolean isActiveProfile(@NonNull String profile) {
		final var environment = getBean(ConfigurableEnvironment.class);
		return environment.acceptsProfiles(Profiles.of(profile));
	}
}

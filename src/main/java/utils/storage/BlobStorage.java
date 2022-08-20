package utils.storage;

import context.ApplicationContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlobStorage {

	public static class Exception extends IOException {

		private static final long serialVersionUID = 101017314280185896L;

		public Exception(final String message, final Throwable cause) {
			super(message, cause);
		}

		public Exception(final String message) {
			super(message);
		}
	}

	public static class PartNotFoundException extends Exception {

		private static final long serialVersionUID = 5530784063527676755L;

		public PartNotFoundException(final String partName) {
			super("Unable to find file part \"" + partName + "\" in request.");
		}
	}

	public interface Service {

		String createTempBlob(String prefix, String suffix) throws Exception;

		void serveBlob(
				HttpServletResponse response,
				String id,
				String downloadName,
				String mimeType,
				boolean attachment)
				throws Exception;

		void storeBlob(HttpServletRequest request, String partName, String id) throws Exception;

		default void storeBlob(MultipartFile file, String id) throws Exception {
			storeBlob(ApplicationContext.getRequest(), file.getName(), id);
		}

		default String storeBlob(MultipartFile file) throws Exception {
			final var id = createTempBlob("", "");
			storeBlob(file, id);
			return id;
		}
	}
}

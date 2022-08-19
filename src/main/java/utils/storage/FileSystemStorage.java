package utils.storage;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import utils.storage.io.IOStreams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileSystemStorage implements BlobStorage.Service {

	@Getter
	private final Path basePath;

	public FileSystemStorage(@NonNull Path basePath) throws IOException {
		this.basePath = basePath;

		if (!(Files.exists(basePath) && Files.isDirectory(basePath)))
			Files.createDirectories(basePath);
	}

	@Override
	public String createTempBlob(String prefix, String suffix) throws BlobStorage.Exception {
		try {
			return this.basePath.relativize(Files.createTempFile(this.basePath, prefix, suffix)).toString();
		} catch (final IOException ex) {
			throw new BlobStorage.Exception("Unable to create temp file.", ex);
		}
	}

	public void serveBlob(HttpServletResponse response, @NonNull String relativePath, boolean attachment)
			throws BlobStorage.Exception {
		final var path = Paths.get(relativePath);
		try {
			serveBlob(response, relativePath, path.getFileName().toString(),
					Files.probeContentType(path), attachment);
		} catch (final IOException ex) {
			throw new BlobStorage.Exception("Unable to serve file.", ex);
		}
	}

	@Override
	public void serveBlob(
			@NonNull HttpServletResponse response,
			@NonNull String relativePath,
			@NonNull String downloadName,
			@NonNull String mimeType,
			boolean attachment)
			throws BlobStorage.Exception {
		final var path = this.basePath.resolve(relativePath);

		try {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
					ContentDisposition
							.builder(attachment ? "attachment" : "inline")
							.filename(downloadName)
							.build().toString());
			response.setContentType(mimeType);
			response.setContentLengthLong(Files.size(path));
			IOStreams.transfer(Files.newInputStream(path), response.getOutputStream(), true, false); // NOSONAR
		} catch (final IOException ex) {
			throw new BlobStorage.Exception("Unable to serve file.", ex);
		}
	}

	@Override
	public void storeBlob(@NonNull HttpServletRequest request, @NonNull String partName, @NonNull String relativePath)
			throws BlobStorage.Exception {
		try {
			final var part = request.getPart(partName);
			if (part == null)
				throw new BlobStorage.PartNotFoundException(partName);

			final var path = this.basePath.resolve(relativePath);
			IOStreams.transfer(
					part.getInputStream(),
					Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), false,
					true);
		} catch (ServletException | IOException ex) {
			throw new BlobStorage.Exception("Unable to store file.", ex);
		}
	}

	@Override
	public void storeBlob(@NonNull MultipartFile file, @NonNull String relativePath) throws BlobStorage.Exception {
		try {
			final var path = this.basePath.resolve(relativePath);
			file.transferTo(path);
		} catch (final IOException ex) {
			throw new BlobStorage.Exception("Unable to store file.", ex);
		}
	}
}

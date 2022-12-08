package service;

import lombok.RequiredArgsConstructor;
import model.ChannelUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserAvatarService implements ServiceClient.AvatarService {

	private static final long					serialVersionUID	= 1653430830133680883L;

	private static final int					WIDTH				= 100;

	private static final int					HEIGHT				= 100;

	private static final String					EXTENSION			= "jpg";

	private final ServiceClient.UserService service;

	@Override
	public Map<ChannelUser, InputStream> getAvatars() throws ServiceClient.Exception {
		return this.service.getUsers().stream()
				.collect(Collectors.toMap(user -> user, user -> {
					try {
						return new ByteArrayInputStream(this.createInitialIcon(user));
					} catch (ServiceClient.Exception  e) {
						return InputStream.nullInputStream();
					}
				}));
	}

	private byte[] createInitialIcon(ChannelUser user) throws ServiceClient.Exception  {
		final String storedInitials = ServiceClient.UserService.getInitials(user.getUserName());
		final String initials = storedInitials.length() > 2 ? storedInitials.substring(0, 2).toUpperCase() : storedInitials.toUpperCase();

		final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setColor(new Color(253, 36, 86));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
		final FontMetrics fontMetrics = graphics.getFontMetrics(graphics.getFont());
		final Rectangle2D rect = fontMetrics.getStringBounds(initials, graphics);
		final int textWidth = (int) (rect.getWidth());
		final int textHeight = (int) (rect.getHeight());
		final int textX = (WIDTH - textWidth) / 2;
		final int textY = ((HEIGHT - textHeight) / 2) + (fontMetrics.getAscent() - 3);
		graphics.drawString(initials, textX, textY);

		try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, EXTENSION, outputStream);
			final byte[] file = outputStream.toByteArray();
			user.setAvatar(file);
			this.service.update(user);
			return file;
		} catch (final IOException e) {
			throw new ServiceClient.Exception ("Unable to create initial Icon.", e);
		}
	}
}

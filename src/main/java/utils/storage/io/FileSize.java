package utils.storage.io;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.util.Pair;
import utils.Strings;

import java.text.DecimalFormat;
import java.util.List;

@AllArgsConstructor
public class FileSize {

	private static final List<Pair<Long, FileSizeUnit>> UNITS = List.of(
			Pair.of(1_000_000_000_000L, FileSizeUnit.TB),
			Pair.of(1_000_000_000L, FileSizeUnit.GB),
			Pair.of(1_000_000L, FileSizeUnit.MB),
			Pair.of(1_000L, FileSizeUnit.KB),
			Pair.of(1L, FileSizeUnit.B));

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.##");

	private long value;

	public FileSize(@NonNull String value) throws InvalidFormatException {
		if (!parseValue(value))
			throw new InvalidFormatException("file size", value);
	}

	private boolean parseValue(String value) {
		for (final var pair : UNITS) {
			final var unit = pair.getSecond();
			final var unitName = unit.name();
			if (Strings.endsWithIgnoreCase(value, unitName)) {
				this.value = Long.parseUnsignedLong(value.substring(0, value.length() - unitName.length()).trim())
						* pair.getFirst();
				return true;
			}
		}
		try {
			this.value = Long.parseUnsignedLong(value);
			return true;
		} catch (final NumberFormatException ex) {
			return false;
		}
	}

	public long toBytes() {
		return this.value;
	}

	@Override
	public String toString() {
		for (final var pair : UNITS) {
			final double result = (double) this.value / pair.getFirst();
			if (result >= 1D)
				return FORMATTER.format(result) + pair.getSecond();
		}
		return Long.toString(this.value);
	}
}

package utils.storage.io;

import fr.agysoft.boot.Strings;
import fr.agysoft.boot.functional.Pair;
import fr.agysoft.boot.text.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.text.DecimalFormat;
import java.util.List;

@AllArgsConstructor
public class FileSize {

	private static final List<Pair<Long, fr.agysoft.boot.io.FileSizeUnit>> UNITS = List.of(
			new Pair<>(1_000_000_000_000L, fr.agysoft.boot.io.FileSizeUnit.TB),
			new Pair<>(1_000_000_000L, fr.agysoft.boot.io.FileSizeUnit.GB),
			new Pair<>(1_000_000L, fr.agysoft.boot.io.FileSizeUnit.MB),
			new Pair<>(1_000L, fr.agysoft.boot.io.FileSizeUnit.KB),
			new Pair<>(1L, fr.agysoft.boot.io.FileSizeUnit.B));

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.##");

	private long value;

	public FileSize(@NonNull String value) throws InvalidFormatException {
		if (!parseValue(value))
			throw new InvalidFormatException("file size", value);
	}

	private boolean parseValue(String value) {
		for (final var pair : UNITS) {
			final var unit = pair.getSecondValue();
			final var unitName = unit.name();
			if (Strings.endsWithIgnoreCase(value, unitName)) {
				this.value = Long.parseUnsignedLong(value.substring(0, value.length() - unitName.length()).trim())
						* pair.getFirstValue();
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
			final double result = (double) this.value / pair.getFirstValue();
			if (result >= 1D)
				return FORMATTER.format(result) + pair.getSecondValue().translation();
		}
		return Long.toString(this.value);
	}
}

package utils;

import lombok.NonNull;

public class ParserException extends IllegalArgumentException {

    private static final long serialVersionUID = 5916886297269143884L;

    public ParserException(@NonNull Parser parser, @NonNull String message) {
        super(Strings.concat(parser.getSource(), "  ", parser.getPosition().toString(), ": ", message));
    }
}

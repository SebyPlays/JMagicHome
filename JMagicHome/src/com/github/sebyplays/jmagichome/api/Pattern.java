package com.github.sebyplays.jmagichome.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Pattern {

    SEVEN_COLOR_CROSS_FADE(0x25),
    RED_GRADUAL_CHANGE(0x26),
    GREEN_GRADUAL_CHANGE(0x27),
    BLUE_GRADUAL_CHANGE(0x28),
    YELLOW_GRADUAL_CHANGE(0x29),
    CYAN_GRADUAL_CHANGE(0x2a),
    PURPLE_GRADUAL_CHANGE(0x2b),
    WHITE_GRADUAL_CHANGE(0x2c),
    RED_GREEN_CROSS_FADE(0x2d),
    RED_BLUE_CROSS_FADE(0x2e),
    GREEN_BLUE_CROSS_FADE(0x2f),
    SEVEN_COLOR_STROBE_FLASH(0x30),
    RED_STROBE_FLASH(0x31),
    GREEN_STROBE_FLASH(0x32),
    BLUE_STROBE_FLASH(0x33),
    YELLOW_STROBE_FLASH(0x34),
    CYAN_STROBE_FLASH(0x35),
    PURPLE_STROBE_FLASH(0x36),
    WHITE_STROBE_FLASH(0x37),
    SEVEN_COLOR_JUMPING(0x38);

    public static Pattern fromId(@NonNull final int id) {
        for (final Pattern pattern : values()) {
            if (pattern.getId() == id) {
                return pattern;
            }
        }
        throw new IllegalArgumentException("No pattern with id " + id);
    }

    @Getter @NonNull private final int id;

}

package com.github.sebyplays.jmagichome.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Transition {

    GRADUAL(0x3a),
    JUMP(0x3b),
    STROBE(0x3c);

    @Getter @NonNull private final int id;

    public static Transition getById(int id) {
        for (Transition transition : values()) {
            if (transition.getId() == id) {
                return transition;
            }
        }
        return null;
    }

}

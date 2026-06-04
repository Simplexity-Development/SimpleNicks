package simplexity.simplenicks.saving;

import java.util.UUID;

public record NicknameRecord (UUID uuid, String username, String nickname, String normalized,
                              boolean isActiveNickname, long lastLogin) {
}

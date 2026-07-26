package simplexity.simplenicks.fabric.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.util.NickPermission;

/**
 * Maps {@link NickPermission} defaults to the correct {@code Permissions.check} fallback.
 * <ul>
 *   <li>{@code "true"}  → granted to all players by default</li>
 *   <li>{@code "op"}    → granted to op-level-2+ by default</li>
 *   <li>{@code "false"} → not granted to anyone by default</li>
 * </ul>
 */
public final class FabricPermissions {

    private FabricPermissions() {
    }

    public static boolean check(@NotNull CommandSourceStack source, @NotNull NickPermission permission) {
        return switch (permission.getPermissionDefault()) {
            case "true" -> Permissions.check(source, permission.getPermissionKey(), true);
            case "false" -> Permissions.check(source, permission.getPermissionKey(), false);
            default -> Permissions.check(source, permission.getPermissionKey(), 2);
        };
    }

    public static boolean check(@NotNull ServerPlayer player, @NotNull NickPermission permission) {
        return switch (permission.getPermissionDefault()) {
            case "true" -> Permissions.check(player, permission.getPermissionKey(), true);
            case "false" -> Permissions.check(player, permission.getPermissionKey(), false);
            default -> Permissions.check(player, permission.getPermissionKey(), 2);
        };
    }

    /**
     * String-key variants used by platform adapters and sender contexts. Looks up the matching
     * {@link NickPermission} to resolve the correct fallback; defaults to op-level-2 if the key
     * is not a registered permission.
     */
    public static boolean check(@NotNull CommandSourceStack source, @NotNull String permissionKey) {
        for (NickPermission perm : NickPermission.values()) {
            if (perm.getPermissionKey().equals(permissionKey)) {
                return check(source, perm);
            }
        }
        return Permissions.check(source, permissionKey, 2);
    }

    public static boolean check(@NotNull ServerPlayer player, @NotNull String permissionKey) {
        for (NickPermission perm : NickPermission.values()) {
            if (perm.getPermissionKey().equals(permissionKey)) {
                return check(player, perm);
            }
        }
        return Permissions.check(player, permissionKey, 2);
    }
}

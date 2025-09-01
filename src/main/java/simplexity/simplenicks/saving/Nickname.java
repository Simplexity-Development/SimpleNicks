package simplexity.simplenicks.saving;

import org.jetbrains.annotations.NotNull;

public class Nickname {
    private String nickname;
    private String normalizedNickname;

    public Nickname(String nickname, String normalizedNickname) {
        this.nickname = nickname;
        this.normalizedNickname = normalizedNickname;
    }

    @NotNull
    public String getNickname(){
        return nickname;
    }

    @NotNull
    public String getNormalizedNickname(){
        return normalizedNickname;
    }

    public void setNickname(@NotNull String newNick){
        nickname = newNick;
    }

    public void setNormalizedNick(@NotNull String newNormalizedNick){
        normalizedNickname = newNormalizedNick;
    }

    @Override
    @NotNull
    public String toString(){
        return "[nickname=" + nickname
        + ", normalizedNickname=" + normalizedNickname
        + "]";
    }

}

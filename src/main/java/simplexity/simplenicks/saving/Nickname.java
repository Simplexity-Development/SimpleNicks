package simplexity.simplenicks.saving;

public class Nickname {
    private String nickname;
    private String normalizedNickname;

    public Nickname(String nickname, String normalizedNickname) {
        this.nickname = nickname;
        this.normalizedNickname = normalizedNickname;
    }

    public String getNickname(){
        return nickname;
    }

    public String getNormalizedNickname(){
        return normalizedNickname;
    }

    public void setNickname(String newNick){
        nickname = newNick;
    }

    public void setNormalizedNick(String newNormalizedNick){
        normalizedNickname = newNormalizedNick;
    }

    @Override
    public String toString(){
        return "[nickname=" + nickname
        + ", normalizedNickname=" + normalizedNickname
        + "]";
    }

}

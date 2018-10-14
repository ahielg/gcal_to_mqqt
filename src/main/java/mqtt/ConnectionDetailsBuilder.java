package mqtt;

public class ConnectionDetailsBuilder {
    private String url;
    private String username;
    private String password;

    public ConnectionDetailsBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ConnectionDetailsBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ConnectionDetailsBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public ConnectionDetails build() {
        return new ConnectionDetails(url, username, password);
    }
}